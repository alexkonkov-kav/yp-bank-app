package contracts.account

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'Post transfer account to account data'
    name 'transfer_account'

    request {
        method POST()
        url '/notification/transfer-account'
        headers {
            contentType(applicationJson())
            header 'Authorization', value(
                    consumer(regex('Bearer\\s+.+')),   // для консьюмера (WireMock): любой Bearer-токен
                    producer('Bearer test-token')  // для провайдера (MockMvc-тест): ровно этот токен
            )
        }
        body(
                fromUsername: 'user1',
                fromName: 'fromName',
                fromAmountOld: 10,
                fromAmountNew: 20,
                toUsername: 'user2',
                toName: 'toName',
                toAmountOld: 10,
                toAmountNew: 20,
                amount: 10
        )
    }

    response {
        status OK()
    }
}