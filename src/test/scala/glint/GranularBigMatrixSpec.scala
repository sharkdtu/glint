package glint

import glint.models.client.granular.GranularBigMatrix
import org.scalatest.{FlatSpec, Matchers}

/**
  * GranularBigMatrix test specification
  */
class GranularBigMatrixSpec extends FlatSpec with SystemTest with Matchers {

  "A GranularBigMatrix" should "handle large push/pull requests" in withMaster { _ =>
    withServers(2) { _ =>
      withClient { client =>
        val model = whenReady(client.matrix[Double](1000, 1000)) { identity }
        val granularModel = new GranularBigMatrix[Double](model, 1000, 200)
        val rows = new Array[Long](1000000)
        val cols = new Array[Int](1000000)
        val values = new Array[Double](1000000)
        var i = 0
        while (i < rows.length) {
          rows(i) = i % 1000
          cols(i) = i / 1000
          values(i) = i * 3.14
          i += 1
        }

        whenReady(granularModel.push(rows, cols, values)) { identity }
        val result = whenReady(granularModel.pull(rows, cols)) { identity }

        result shouldEqual values
      }
    }
  }

}
