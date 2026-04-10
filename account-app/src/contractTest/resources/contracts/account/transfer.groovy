package contracts.account

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'Post transfer to id'
    name 'transfer'

    request {
        method POST()
        url '/accounts/transfer'
        headers {
            contentType(applicationJson())
            header 'Authorization', value(
                    consumer(regex('Bearer\\s+.+')),   // для консьюмера (WireMock): любой Bearer-токен
                    producer('Bearer test-token')  // для провайдера (MockMvc-тест): ровно этот токен
            )
        }
        body(
                fromId: 1,
                toId: 2,
                value: 100
        )
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(
                login: 'user1',
                name: 'test user',
                birthDate: '1990-01-01',
                balance: 1000
        )
    }
}