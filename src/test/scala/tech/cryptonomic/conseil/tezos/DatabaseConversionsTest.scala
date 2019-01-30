package tech.cryptonomic.conseil.tezos

import org.scalatest.{Matchers, WordSpec, OptionValues}
import org.scalatest.Inspectors._
import tech.cryptonomic.conseil.tezos.TezosTypes._
import tech.cryptonomic.conseil.util.{ConversionSyntax, RandomSeed}

class DatabaseConversionsTest
  extends WordSpec
  with Matchers
  with OptionValues
  with TezosDataGeneration
  with DBConversionsData {

  "The database conversion" should {

    implicit val seed = RandomSeed(testReferenceTime.getTime)

    val groupHash = OperationHash("operationhash")

    //keep level 1, dropping the genesis block
    val block = generateBlocks(toLevel = 1, startAt = testReferenceTime).drop(1).head

    val sut = DatabaseConversions

    "correctly convert a positive bignumber valued '0' from tezos models to a BigDecimal value" in {
      sut.extractBigDecimal(PositiveDecimal(0)).value shouldBe BigDecimal(0)
    }

    "correctly convert a positive bignumber from tezos models to a BigDecimal value" in {
      sut.extractBigDecimal(PositiveDecimal(1000)).value shouldBe BigDecimal(1000)
    }

    "give no result when converting invalid positive bignumbers from tezos models to a BigDecimal value" in {
      sut.extractBigDecimal(InvalidPositiveDecimal("1000A")) shouldBe 'empty
    }

    "convert an Endorsement to a database row" in {
      import ConversionSyntax._
      import DatabaseConversions._

      val converted = (block, groupHash, sampleEndorsement: Operation).convertTo[Tables.OperationsRow]

      //terrible to look at, but that's what we get from the standard slick's HList representation
      val operationId = converted(0)
      val operationGroupHash = converted(1)
      val kind = converted(2)
      val level = converted(3)
      val delegate = converted(4)
      val slots = converted(5)
      val nonce = converted(6)
      val pkh = converted(7)
      val secret = converted(8)
      val source = converted(9)
      val fee = converted(10)
      val counter = converted(11)
      val gasLimit = converted(12)
      val storageLimit = converted(13)
      val publicKey = converted(14)
      val amount = converted(15)
      val destination = converted(16)
      val parameters = converted(17)
      val managerPubkey = converted(18)
      val balance = converted(19)
      val spendable = converted(20)
      val delegatable = converted(21)
      val script = converted(22)
      val status = converted(23)
      val blockHash = converted(24)
      val blockLevel = converted(25)
      val timestamp = converted(26)

      operationId shouldBe 0
      operationGroupHash shouldBe groupHash.value
      blockHash shouldBe block.metadata.hash.value
      blockLevel shouldBe block.metadata.header.level
      timestamp shouldBe block.metadata.header.timestamp
      kind shouldBe "endorsement"
      level.value shouldBe sampleEndorsement.level
      delegate.value shouldBe sampleEndorsement.metadata.delegate.value
      slots.value shouldBe "[29,27,20,17]"

      forAll(
        nonce ::
        pkh ::
        secret ::
        source ::
        fee ::
        counter ::
        gasLimit ::
        storageLimit ::
        publicKey ::
        amount ::
        destination ::
        parameters ::
        managerPubkey ::
        balance ::
        spendable ::
        delegatable ::
        script ::
        status :: Nil) {
        _ shouldBe 'empty
      }

    }

    "convert a SeedNonceRevelation to a database row" in {
      import ConversionSyntax._
      import DatabaseConversions._

      val converted = (block, groupHash, sampleNonceRevelation: Operation).convertTo[Tables.OperationsRow]

      //terrible to look at, but that's what we get from the standard slick's HList representation
      val operationId = converted(0)
      val operationGroupHash = converted(1)
      val kind = converted(2)
      val level = converted(3)
      val delegate = converted(4)
      val slots = converted(5)
      val nonce = converted(6)
      val pkh = converted(7)
      val secret = converted(8)
      val source = converted(9)
      val fee = converted(10)
      val counter = converted(11)
      val gasLimit = converted(12)
      val storageLimit = converted(13)
      val publicKey = converted(14)
      val amount = converted(15)
      val destination = converted(16)
      val parameters = converted(17)
      val managerPubkey = converted(18)
      val balance = converted(19)
      val spendable = converted(20)
      val delegatable = converted(21)
      val script = converted(22)
      val status = converted(23)
      val blockHash = converted(24)
      val blockLevel = converted(25)
      val timestamp = converted(26)

      operationId shouldBe 0
      operationGroupHash shouldBe groupHash.value
      blockHash shouldBe block.metadata.hash.value
      blockLevel shouldBe block.metadata.header.level
      timestamp shouldBe block.metadata.header.timestamp
      kind shouldBe "seed_nonce_revelation"
      level.value shouldBe sampleNonceRevelation.level
      nonce.value shouldBe sampleNonceRevelation.nonce.value

      forAll(
        delegate ::
        slots ::
        pkh ::
        secret ::
        source ::
        fee ::
        counter ::
        gasLimit ::
        storageLimit ::
        publicKey ::
        amount ::
        destination ::
        parameters ::
        managerPubkey ::
        balance ::
        spendable ::
        delegatable ::
        script ::
        status :: Nil) {
        _ shouldBe 'empty
      }

    }

    "convert an ActivateAccount to a database row" in {
      import ConversionSyntax._
      import DatabaseConversions._

      val converted = (block, groupHash, sampleAccountActivation: Operation).convertTo[Tables.OperationsRow]

      //terrible to look at, but that's what we get from the standard slick's HList representation
      val operationId = converted(0)
      val operationGroupHash = converted(1)
      val kind = converted(2)
      val level = converted(3)
      val delegate = converted(4)
      val slots = converted(5)
      val nonce = converted(6)
      val pkh = converted(7)
      val secret = converted(8)
      val source = converted(9)
      val fee = converted(10)
      val counter = converted(11)
      val gasLimit = converted(12)
      val storageLimit = converted(13)
      val publicKey = converted(14)
      val amount = converted(15)
      val destination = converted(16)
      val parameters = converted(17)
      val managerPubkey = converted(18)
      val balance = converted(19)
      val spendable = converted(20)
      val delegatable = converted(21)
      val script = converted(22)
      val status = converted(23)
      val blockHash = converted(24)
      val blockLevel = converted(25)
      val timestamp = converted(26)

      operationId shouldBe 0
      operationGroupHash shouldBe groupHash.value
      blockHash shouldBe block.metadata.hash.value
      blockLevel shouldBe block.metadata.header.level
      timestamp shouldBe block.metadata.header.timestamp
      kind shouldBe "activate_account"
      pkh.value shouldBe sampleAccountActivation.pkh.value
      secret.value shouldBe sampleAccountActivation.secret.value

      forAll(
        level ::
        delegate ::
        slots ::
        nonce ::
        source ::
        fee ::
        counter ::
        gasLimit ::
        storageLimit ::
        publicKey ::
        amount ::
        destination ::
        parameters ::
        managerPubkey ::
        balance ::
        spendable ::
        delegatable ::
        script ::
        status :: Nil) {
        _ shouldBe 'empty
      }

    }

    "convert a Reveal to a database row" in {
      import ConversionSyntax._
      import DatabaseConversions._

      val converted = (block, groupHash, sampleReveal: Operation).convertTo[Tables.OperationsRow]

      //terrible to look at, but that's what we get from the standard slick's HList representation
      val operationId = converted(0)
      val operationGroupHash = converted(1)
      val kind = converted(2)
      val level = converted(3)
      val delegate = converted(4)
      val slots = converted(5)
      val nonce = converted(6)
      val pkh = converted(7)
      val secret = converted(8)
      val source = converted(9)
      val fee = converted(10)
      val counter = converted(11)
      val gasLimit = converted(12)
      val storageLimit = converted(13)
      val publicKey = converted(14)
      val amount = converted(15)
      val destination = converted(16)
      val parameters = converted(17)
      val managerPubkey = converted(18)
      val balance = converted(19)
      val spendable = converted(20)
      val delegatable = converted(21)
      val script = converted(22)
      val status = converted(23)
      val blockHash = converted(24)
      val blockLevel = converted(25)
      val timestamp = converted(26)

      operationId shouldBe 0
      operationGroupHash shouldBe groupHash.value
      blockHash shouldBe block.metadata.hash.value
      blockLevel shouldBe block.metadata.header.level
      timestamp shouldBe block.metadata.header.timestamp
      kind shouldBe "reveal"
      source.value shouldBe sampleReveal.source.id
      sampleReveal.fee match {
        case PositiveDecimal(bignumber) => fee.value shouldBe bignumber
        case _ => fee shouldBe 'empty
      }
      sampleReveal.counter match {
        case PositiveDecimal(bignumber) => counter.value shouldBe bignumber
        case _ => counter shouldBe 'empty
      }
      sampleReveal.gas_limit match {
        case PositiveDecimal(bignumber) => gasLimit.value shouldBe bignumber
        case _ => gasLimit shouldBe 'empty
      }
      sampleReveal.storage_limit match {
        case PositiveDecimal(bignumber) => storageLimit.value shouldBe bignumber
        case _ => storageLimit shouldBe 'empty
      }
      publicKey.value shouldBe sampleReveal.public_key.value
      status.value shouldBe sampleReveal.metadata.operation_result.status

      forAll(
        level ::
        delegate ::
        slots ::
        nonce ::
        pkh ::
        secret ::
        amount ::
        destination ::
        parameters ::
        managerPubkey ::
        balance ::
        spendable ::
        delegatable ::
        script :: Nil) {
        _ shouldBe 'empty
      }

    }

    "convert a Transaction to a database row" in {
      import ConversionSyntax._
      import DatabaseConversions._

      val converted = (block, groupHash, sampleTransaction: Operation).convertTo[Tables.OperationsRow]

      //terrible to look at, but that's what we get from the standard slick's HList representation
      val operationId = converted(0)
      val operationGroupHash = converted(1)
      val kind = converted(2)
      val level = converted(3)
      val delegate = converted(4)
      val slots = converted(5)
      val nonce = converted(6)
      val pkh = converted(7)
      val secret = converted(8)
      val source = converted(9)
      val fee = converted(10)
      val counter = converted(11)
      val gasLimit = converted(12)
      val storageLimit = converted(13)
      val publicKey = converted(14)
      val amount = converted(15)
      val destination = converted(16)
      val parameters = converted(17)
      val managerPubkey = converted(18)
      val balance = converted(19)
      val spendable = converted(20)
      val delegatable = converted(21)
      val script = converted(22)
      val status = converted(23)
      val blockHash = converted(24)
      val blockLevel = converted(25)
      val timestamp = converted(26)

      operationId shouldBe 0
      operationGroupHash shouldBe groupHash.value
      blockHash shouldBe block.metadata.hash.value
      blockLevel shouldBe block.metadata.header.level
      timestamp shouldBe block.metadata.header.timestamp
      kind shouldBe "transaction"
      source.value shouldBe sampleTransaction.source.id
      sampleTransaction.fee match {
        case PositiveDecimal(bignumber) => fee.value shouldBe bignumber
        case _ => fee shouldBe 'empty
      }
      sampleTransaction.counter match {
        case PositiveDecimal(bignumber) => counter.value shouldBe bignumber
        case _ => counter shouldBe 'empty
      }
      sampleTransaction.gas_limit match {
        case PositiveDecimal(bignumber) => gasLimit.value shouldBe bignumber
        case _ => gasLimit shouldBe 'empty
      }
      sampleTransaction.storage_limit match {
        case PositiveDecimal(bignumber) => storageLimit.value shouldBe bignumber
        case _ => storageLimit shouldBe 'empty
      }
      sampleTransaction.amount match {
        case PositiveDecimal(bignumber) => amount.value shouldBe bignumber
        case _ => amount shouldBe 'empty
      }
      destination.value shouldBe sampleTransaction.destination.id
      parameters shouldBe sampleTransaction.parameters.map(_.expression)
      status.value shouldBe sampleTransaction.metadata.operation_result.status

      forAll(
        level ::
        delegate ::
        slots ::
        nonce ::
        pkh ::
        secret ::
        publicKey ::
        managerPubkey ::
        balance ::
        spendable ::
        delegatable ::
        script :: Nil) {
        _ shouldBe 'empty
      }

    }

    "convert an Origination to a database row" in {
      import ConversionSyntax._
      import DatabaseConversions._

      val converted = (block, groupHash, sampleOrigination: Operation).convertTo[Tables.OperationsRow]

      //terrible to look at, but that's what we get from the standard slick's HList representation
      val operationId = converted(0)
      val operationGroupHash = converted(1)
      val kind = converted(2)
      val level = converted(3)
      val delegate = converted(4)
      val slots = converted(5)
      val nonce = converted(6)
      val pkh = converted(7)
      val secret = converted(8)
      val source = converted(9)
      val fee = converted(10)
      val counter = converted(11)
      val gasLimit = converted(12)
      val storageLimit = converted(13)
      val publicKey = converted(14)
      val amount = converted(15)
      val destination = converted(16)
      val parameters = converted(17)
      val managerPubkey = converted(18)
      val balance = converted(19)
      val spendable = converted(20)
      val delegatable = converted(21)
      val script = converted(22)
      val status = converted(23)
      val blockHash = converted(24)
      val blockLevel = converted(25)
      val timestamp = converted(26)

      operationId shouldBe 0
      operationGroupHash shouldBe groupHash.value
      blockHash shouldBe block.metadata.hash.value
      blockLevel shouldBe block.metadata.header.level
      timestamp shouldBe block.metadata.header.timestamp
      kind shouldBe "origination"
      delegate shouldBe sampleOrigination.delegate.map(_.value)
      source.value shouldBe sampleOrigination.source.id
      sampleOrigination.fee match {
        case PositiveDecimal(bignumber) => fee.value shouldBe bignumber
        case _ => fee shouldBe 'empty
      }
      sampleOrigination.counter match {
        case PositiveDecimal(bignumber) => counter.value shouldBe bignumber
        case _ => counter shouldBe 'empty
      }
      sampleOrigination.gas_limit match {
        case PositiveDecimal(bignumber) => gasLimit.value shouldBe bignumber
        case _ => gasLimit shouldBe 'empty
      }
      sampleOrigination.storage_limit match {
        case PositiveDecimal(bignumber) => storageLimit.value shouldBe bignumber
        case _ => storageLimit shouldBe 'empty
      }
      sampleOrigination.balance match {
        case PositiveDecimal(bignumber) => balance.value shouldBe bignumber
        case _ => balance shouldBe 'empty
      }
      managerPubkey.value shouldBe sampleOrigination.manager_pubkey.value
      spendable shouldBe sampleOrigination.spendable
      delegatable shouldBe sampleOrigination.delegatable
      script shouldBe sampleOrigination.script.map(_.code.expression)
      status.value shouldBe sampleOrigination.metadata.operation_result.status

      forAll(
        level ::
        slots ::
        nonce ::
        pkh ::
        secret ::
        publicKey ::
        amount ::
        destination ::
        parameters :: Nil) {
        _ shouldBe 'empty
      }

    }

    "convert an Delegation to a database row" in {
      import ConversionSyntax._
      import DatabaseConversions._

      val converted = (block, groupHash, sampleDelegation: Operation).convertTo[Tables.OperationsRow]

      //terrible to look at, but that's what we get from the standard slick's HList representation
      val operationId = converted(0)
      val operationGroupHash = converted(1)
      val kind = converted(2)
      val level = converted(3)
      val delegate = converted(4)
      val slots = converted(5)
      val nonce = converted(6)
      val pkh = converted(7)
      val secret = converted(8)
      val source = converted(9)
      val fee = converted(10)
      val counter = converted(11)
      val gasLimit = converted(12)
      val storageLimit = converted(13)
      val publicKey = converted(14)
      val amount = converted(15)
      val destination = converted(16)
      val parameters = converted(17)
      val managerPubkey = converted(18)
      val balance = converted(19)
      val spendable = converted(20)
      val delegatable = converted(21)
      val script = converted(22)
      val status = converted(23)
      val blockHash = converted(24)
      val blockLevel = converted(25)
      val timestamp = converted(26)

      operationId shouldBe 0
      operationGroupHash shouldBe groupHash.value
      blockHash shouldBe block.metadata.hash.value
      blockLevel shouldBe block.metadata.header.level
      timestamp shouldBe block.metadata.header.timestamp
      kind shouldBe "delegation"
      delegate shouldBe sampleDelegation.delegate.map(_.value)
      source.value shouldBe sampleDelegation.source.id
      sampleDelegation.fee match {
        case PositiveDecimal(bignumber) => fee.value shouldBe bignumber
        case _ => fee shouldBe 'empty
      }
      sampleDelegation.counter match {
        case PositiveDecimal(bignumber) => counter.value shouldBe bignumber
        case _ => counter shouldBe 'empty
      }
      sampleDelegation.gas_limit match {
        case PositiveDecimal(bignumber) => gasLimit.value shouldBe bignumber
        case _ => gasLimit shouldBe 'empty
      }
      sampleDelegation.storage_limit match {
        case PositiveDecimal(bignumber) => storageLimit.value shouldBe bignumber
        case _ => storageLimit shouldBe 'empty
      }
      status.value shouldBe sampleDelegation.metadata.operation_result.status

      forAll(
        level ::
        slots ::
        nonce ::
        pkh ::
        secret ::
        publicKey ::
        amount ::
        destination ::
        parameters ::
        managerPubkey ::
        balance ::
        spendable ::
        delegatable ::
        script :: Nil) {
        _ shouldBe 'empty
      }

    }

    "convert an DoubleEndorsementEvidence to a database row" in {
      import ConversionSyntax._
      import DatabaseConversions._

      val converted = (block, groupHash, DoubleEndorsementEvidence: Operation).convertTo[Tables.OperationsRow]

      //terrible to look at, but that's what we get from the standard slick's HList representation
      val operationId = converted(0)
      val operationGroupHash = converted(1)
      val kind = converted(2)
      val level = converted(3)
      val delegate = converted(4)
      val slots = converted(5)
      val nonce = converted(6)
      val pkh = converted(7)
      val secret = converted(8)
      val source = converted(9)
      val fee = converted(10)
      val counter = converted(11)
      val gasLimit = converted(12)
      val storageLimit = converted(13)
      val publicKey = converted(14)
      val amount = converted(15)
      val destination = converted(16)
      val parameters = converted(17)
      val managerPubkey = converted(18)
      val balance = converted(19)
      val spendable = converted(20)
      val delegatable = converted(21)
      val script = converted(22)
      val status = converted(23)
      val blockHash = converted(24)
      val blockLevel = converted(25)
      val timestamp = converted(26)

      operationId shouldBe 0
      operationGroupHash shouldBe groupHash.value
      blockHash shouldBe block.metadata.hash.value
      blockLevel shouldBe block.metadata.header.level
      timestamp shouldBe block.metadata.header.timestamp
      kind shouldBe "double_endorsement_evidence"

      forAll(
        level ::
        delegate ::
        slots ::
        nonce ::
        pkh ::
        secret ::
        source ::
        fee ::
        counter ::
        gasLimit ::
        storageLimit ::
        publicKey ::
        amount ::
        destination ::
        parameters ::
        managerPubkey ::
        balance ::
        spendable ::
        delegatable ::
        script ::
        status :: Nil) {
        _ shouldBe 'empty
      }

    }

    "convert an DoubleBakingEvidence to a database row" in {
      import ConversionSyntax._
      import DatabaseConversions._

      val converted = (block, groupHash, DoubleBakingEvidence: Operation).convertTo[Tables.OperationsRow]

      //terrible to look at, but that's what we get from the standard slick's HList representation
      val operationId = converted(0)
      val operationGroupHash = converted(1)
      val kind = converted(2)
      val level = converted(3)
      val delegate = converted(4)
      val slots = converted(5)
      val nonce = converted(6)
      val pkh = converted(7)
      val secret = converted(8)
      val source = converted(9)
      val fee = converted(10)
      val counter = converted(11)
      val gasLimit = converted(12)
      val storageLimit = converted(13)
      val publicKey = converted(14)
      val amount = converted(15)
      val destination = converted(16)
      val parameters = converted(17)
      val managerPubkey = converted(18)
      val balance = converted(19)
      val spendable = converted(20)
      val delegatable = converted(21)
      val script = converted(22)
      val status = converted(23)
      val blockHash = converted(24)
      val blockLevel = converted(25)
      val timestamp = converted(26)

      operationId shouldBe 0
      operationGroupHash shouldBe groupHash.value
      blockHash shouldBe block.metadata.hash.value
      blockLevel shouldBe block.metadata.header.level
      timestamp shouldBe block.metadata.header.timestamp
      kind shouldBe "double_baking_evidence"

      forAll(
        level ::
        delegate ::
        slots ::
        nonce ::
        pkh ::
        secret ::
        source ::
        fee ::
        counter ::
        gasLimit ::
        storageLimit ::
        publicKey ::
        amount ::
        destination ::
        parameters ::
        managerPubkey ::
        balance ::
        spendable ::
        delegatable ::
        script ::
        status :: Nil) {
        _ shouldBe 'empty
      }

    }

    "convert an Proposals to a database row" in {
      import ConversionSyntax._
      import DatabaseConversions._

      val converted = (block, groupHash, Proposals: Operation).convertTo[Tables.OperationsRow]

      //terrible to look at, but that's what we get from the standard slick's HList representation
      val operationId = converted(0)
      val operationGroupHash = converted(1)
      val kind = converted(2)
      val level = converted(3)
      val delegate = converted(4)
      val slots = converted(5)
      val nonce = converted(6)
      val pkh = converted(7)
      val secret = converted(8)
      val source = converted(9)
      val fee = converted(10)
      val counter = converted(11)
      val gasLimit = converted(12)
      val storageLimit = converted(13)
      val publicKey = converted(14)
      val amount = converted(15)
      val destination = converted(16)
      val parameters = converted(17)
      val managerPubkey = converted(18)
      val balance = converted(19)
      val spendable = converted(20)
      val delegatable = converted(21)
      val script = converted(22)
      val status = converted(23)
      val blockHash = converted(24)
      val blockLevel = converted(25)
      val timestamp = converted(26)

      operationId shouldBe 0
      operationGroupHash shouldBe groupHash.value
      blockHash shouldBe block.metadata.hash.value
      blockLevel shouldBe block.metadata.header.level
      timestamp shouldBe block.metadata.header.timestamp
      kind shouldBe "proposals"

      forAll(
        level ::
        delegate ::
        slots ::
        nonce ::
        pkh ::
        secret ::
        source ::
        fee ::
        counter ::
        gasLimit ::
        storageLimit ::
        publicKey ::
        amount ::
        destination ::
        parameters ::
        managerPubkey ::
        balance ::
        spendable ::
        delegatable ::
        script ::
        status :: Nil) {
        _ shouldBe 'empty
      }

    }

    "convert an Ballot to a database row" in {
      import ConversionSyntax._
      import DatabaseConversions._

      val converted = (block, groupHash, Ballot: Operation).convertTo[Tables.OperationsRow]

      //terrible to look at, but that's what we get from the standard slick's HList representation
      val operationId = converted(0)
      val operationGroupHash = converted(1)
      val kind = converted(2)
      val level = converted(3)
      val delegate = converted(4)
      val slots = converted(5)
      val nonce = converted(6)
      val pkh = converted(7)
      val secret = converted(8)
      val source = converted(9)
      val fee = converted(10)
      val counter = converted(11)
      val gasLimit = converted(12)
      val storageLimit = converted(13)
      val publicKey = converted(14)
      val amount = converted(15)
      val destination = converted(16)
      val parameters = converted(17)
      val managerPubkey = converted(18)
      val balance = converted(19)
      val spendable = converted(20)
      val delegatable = converted(21)
      val script = converted(22)
      val status = converted(23)
      val blockHash = converted(24)
      val blockLevel = converted(25)
      val timestamp = converted(26)

      operationId shouldBe 0
      operationGroupHash shouldBe groupHash.value
      blockHash shouldBe block.metadata.hash.value
      blockLevel shouldBe block.metadata.header.level
      timestamp shouldBe block.metadata.header.timestamp
      kind shouldBe "ballot"

      forAll(
        level ::
        delegate ::
        slots ::
        nonce ::
        pkh ::
        secret ::
        source ::
        fee ::
        counter ::
        gasLimit ::
        storageLimit ::
        publicKey ::
        amount ::
        destination ::
        parameters ::
        managerPubkey ::
        balance ::
        spendable ::
        delegatable ::
        script ::
        status :: Nil) {
        _ shouldBe 'empty
      }

    }

  }
}