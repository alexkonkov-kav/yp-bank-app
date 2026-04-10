package contracts.account

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'Post edit cash account id'
    name 'edit_cash'

    request {
        method POST()
        url '/accounts/editCash'
        headers {
            contentType(applicationJson())
            header 'Authorization', value(
                    consumer(regex('Bearer\\s+.+')),   // для консьюмера (WireMock): любой Bearer-токен
                    producer('Bearer test-token')  // для провайдера (MockMvc-тест): ровно этот токен
            )
        }
        body(
                accountId: 1,
                value: 2,
                action: "PUT"
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