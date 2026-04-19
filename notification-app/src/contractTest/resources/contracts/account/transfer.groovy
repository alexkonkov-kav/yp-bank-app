package contracts.account

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'Post transfer to id'
    name 'transfer'

    request {
        method POST()
        url '/notification/transfer'
        headers {
            contentType(applicationJson())
            header 'Authorization', value(
                    consumer(regex('Bearer\\s+.+')),   // для консьюмера (WireMock): любой Bearer-токен
                    producer('Bearer test-token')  // для провайдера (MockMvc-тест): ровно этот токен
            )
        }
        body(
                fromUsername: 'user1',
                fromId: 1,
                toId: 2,
                amount: 10
        )
    }

    response {
        status OK()
    }
}