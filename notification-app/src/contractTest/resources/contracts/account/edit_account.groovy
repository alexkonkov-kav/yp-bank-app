package contracts.account

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description 'Post edit account data'
    name 'edit_account'

    request {
        method POST()
        url '/notification/account'
        headers {
            contentType(applicationJson())
            header 'Authorization', value(
                    consumer(regex('Bearer\\s+.+')),   // для консьюмера (WireMock): любой Bearer-токен
                    producer('Bearer test-token')  // для провайдера (MockMvc-тест): ровно этот токен
            )
        }
        body(
                username: 'user1',
                oldName: 'oldTest',
                oldBirthdate: '1990-01-01',
                newName: 'newTest',
                newBirthdate: '1991-01-01'
        )
    }

    response {
        status OK()
    }
}