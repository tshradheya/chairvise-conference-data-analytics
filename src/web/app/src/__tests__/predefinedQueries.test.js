import PredefinedQueries from "@/store/data/predefinedQueries"


// To run tests in watch mode: npm run test:unit -- -t 'Predefined Queries' --watch

describe('Predefined Queries', () => {

    const keysPresent = ['name', 'group', 'data']
    const keysPresentInData = ['type', 'title', 'dataSet', 'conferenceName', 'description', 'selections', 'involvedRecords',
            'filters', 'joiners', 'groupers', 'sorters', 'extraData'];
  
    test('Check if data consistent', () => {
        expect(PredefinedQueries).toBeDefined();
        expect(PredefinedQueries).not.toBeNull();

        for (let query in PredefinedQueries) {
            for (const key of keysPresent) {
                expect(PredefinedQueries[query]).toHaveProperty(key)
                if (key === 'data') {
                    for (const dataKeys of keysPresentInData) {
                        expect(PredefinedQueries[query][key]).toHaveProperty(dataKeys)
                    }
                }
            }
        }
    })

    test('Placeholder for dataSet And conferenceName', () => {
        for (let query in PredefinedQueries) {
            expect(PredefinedQueries[query].data.dataSet).toEqual('${PLACEHOLDER_DATA_SET}')
            expect(PredefinedQueries[query].data.conferenceName).toEqual('${PLACEHOLDER_CONFERENCE_NAME}')
        }
    })
});