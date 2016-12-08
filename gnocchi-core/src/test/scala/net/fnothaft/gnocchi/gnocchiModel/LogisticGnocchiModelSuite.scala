/**
 * Copyright 2016 Frank Austin Nothaft, Taner Dagdelen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.fnothaft.gnocchi.gnocchiModel

import breeze.linalg.DenseVector
import net.fnothaft.gnocchi.GnocchiFunSuite
import net.fnothaft.gnocchi.gnocchiModel.BuildAdditiveLogisticVariantModel
import org.bdgenomics.adam.models.ReferenceRegion

class LogisticGnocchiModelSuite extends GnocchiFunSuite {

  sparkTest("Build AdditiveLogisticGnocchiModel from data and update") {
    // read in the data from binary.csv
    // data comes from: http://www.ats.ucla.edu/stat/sas/dae/binary.sas7bdat
    // results can be found here: http://www.ats.ucla.edu/stat/sas/dae/logit.htm
    val pathToFile = ClassLoader.getSystemClassLoader.getResource("binary.csv").getFile
    val csv = sc.textFile(pathToFile)
    val data = csv.map(line => line.split(",").map(elem => elem.toDouble)) //get rows

    // transform it into the right format
    val observations = data.map(row => {
      val geno: Double = row(0)
      val covars: Array[Double] = row.slice(1, 3)
      val phenos: Array[Double] = Array(row(3)) ++ covars
      (geno, phenos)
    }).collect()
    val altAllele = "No allele"
    val phenotype = "acceptance"
    val locus = ReferenceRegion("Name", 1, 2)
    val scOption = Option(sc)

    // break observations into initial group and group to use in update 
    val initial = observations.slice(0, 10)
    val forUpdate = observations.slice(10, observations.length)

    // feed it into logisitic regression and compare the Wald Chi Squared tests
    val variantModel = BuildAdditiveLogisticVariantModel(initial, locus, altAllele, phenotype)
    variantModel.update(forUpdate)

    // TODO: the checks shouldn't be similar weights but a good accuracy

    // Assert that the weights are correct within a threshold.
    val estWeights: Array[Double] = variantModel.weights :+ variantModel.intercept
    val compWeights = Array(-3.4495484, .0022939, .77701357, -0.5600314)
    for (i <- 0 until 3) {
      assert(estWeights(i) <= (compWeights(i) + 1), s"Weight $i incorrect")
      assert(estWeights(i) >= (compWeights(i) - 1), s"Weight $i incorrect")
    }
    assert(variantModel.numSamples == observations.length, s"NumSamplse incorrect: $variantModel.numSamples vs $observations.length")
  }

  sparkTest("[NOT IMPLEMENTED YET] Build logistic gnocchi model, update, and check results.") {

    // break data up into initial and update

    // build a bunch of variant models, some with qr, some without different

    // build a logisitic Gnocchi model with the variant models

    // run update on the Gnocchi Model

    // check that the variant's get updated correctly

    // check that the variant's that should have been recomputed using qr did get recomputed using qr

    // check that variant's got flagged when they were supposed to.

  }

  sparkTest("[NOT IMPLEMENTED YET] Load logistic gnocchi model, update, and check results.") {

    // break data up into initial and update

    // build a bunch of variant models, some with qr, some without different

    // build a logisitic Gnocchi model with the variant models

    // save the model

    // load the model

    // run update on the Gnocchi Model

    // check that the variant's get updated correctly

    // check that the variant's that should have been recomputed using qr did get recomputed using qr

    // check that variant's got flagged when they were supposed to.

  }
}
