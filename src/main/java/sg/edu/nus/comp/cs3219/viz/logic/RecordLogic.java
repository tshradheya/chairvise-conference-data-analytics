package sg.edu.nus.comp.cs3219.viz.logic;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.AuthorRecord;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.ReviewRecord;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.SubmissionAuthorRecord;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.SubmissionRecord;
import sg.edu.nus.comp.cs3219.viz.storage.repository.AuthorRecordRepository;
import sg.edu.nus.comp.cs3219.viz.storage.repository.ReviewRecordRepository;
import sg.edu.nus.comp.cs3219.viz.storage.repository.SubmissionAuthorRecordRepository;
import sg.edu.nus.comp.cs3219.viz.storage.repository.SubmissionRecordRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecordLogic {
    private AuthorRecordRepository authorRecordRepository;

    private SubmissionRecordRepository submissionRecordRepository;

    private SubmissionAuthorRecordRepository submissionAuthorRecordRepository;

    private ReviewRecordRepository reviewRecordRepository;

    public RecordLogic(AuthorRecordRepository authorRecordRepository,
                       SubmissionRecordRepository submissionRecordRepository,
                       SubmissionAuthorRecordRepository submissionAuthorRecordRepository,
                       ReviewRecordRepository reviewRecordRepository) {
        this.authorRecordRepository = authorRecordRepository;
        this.submissionRecordRepository = submissionRecordRepository;
        this.submissionAuthorRecordRepository = submissionAuthorRecordRepository;
        this.reviewRecordRepository = reviewRecordRepository;
    }

    @Transactional
    public void removeAndPersistAuthorRecordForDataSet(String dataSet, List<AuthorRecord> authorRecordList, String confName) {
        authorRecordRepository.deleteAllByDataSetEqualsAndConferenceNameEquals(dataSet, confName);
        authorRecordRepository.saveAll(authorRecordList.stream().peek(r -> {
            // should not set ID when creating records
            r.setId(null);
            // should set dataSet
            r.setDataSet(dataSet);
            // the other field can be arbitrary
            r.setConferenceName(confName);
        }).collect(Collectors.toList()));
    }

    @Transactional
    public void removeAndPersistReviewRecordForDataSet(String dataSet, List<ReviewRecord> reviewRecordList, String confName) {
        reviewRecordRepository.deleteAllByDataSetEqualsAndConferenceNameEquals(dataSet, confName);
        reviewRecordRepository.saveAll(reviewRecordList.stream().peek(r -> {
            // should not set ID when creating records
            r.setId(null);
            // should set dataSet
            r.setDataSet(dataSet);
            // the other field can be arbitrary
            r.setConferenceName(confName);
        }).collect(Collectors.toList()));
    }

    @Transactional
    public void removeAndPersistSubmissionRecordForDataSet(String dataSet, List<SubmissionRecord> submissionRecordList, String confName) {
        submissionRecordRepository.deleteAllByDataSetEqualsAndConferenceNameEquals(dataSet, confName);
        submissionAuthorRecordRepository.deleteAllByDataSetEqualsAndConferenceNameEquals(dataSet, confName);
        submissionRecordRepository.saveAll(submissionRecordList.stream().peek(s -> {
            // should not set ID when creating records
            s.setId(null);
            // should set dataSet
            s.setDataSet(dataSet);
            s.setConferenceName(confName);
            // create many to many relationship for authors
            List<SubmissionAuthorRecord> submissionAuthorRecords = s.getAuthors().stream()
                    .map(authorName -> {
                        SubmissionAuthorRecord existing = submissionAuthorRecordRepository.findFirstByNameEqualsAndDataSetEquals(authorName, dataSet);
                        if (existing == null) {
                            existing = new SubmissionAuthorRecord();
                            existing.setDataSet(dataSet);
                            existing.setConferenceName(confName);
                            existing.setName(authorName);
                            existing = submissionAuthorRecordRepository.save(existing);
                        }
                        return existing;
                    })
                    .collect(Collectors.toList());
            s.setAuthorSet(submissionAuthorRecords);
            // the other field can be arbitrary
        }).collect(Collectors.toList()));
    }

    @Transactional
    public List<String> getAllConferenceNames(String dataSet) {
        List<String> conferenceNames = new ArrayList<>();

        List<AuthorRecord> conferencesFromAuthorRecord = authorRecordRepository.findByDataSetEquals(dataSet);
        for (AuthorRecord ar: conferencesFromAuthorRecord) {
            if (!conferenceNames.contains(ar.getConferenceName())) {
                conferenceNames.add(ar.getConferenceName());
            }
        }

        List<ReviewRecord> conferencesFromReviewRecord = reviewRecordRepository.findByDataSetEquals(dataSet);
        for (ReviewRecord rr: conferencesFromReviewRecord) {
            if (!conferenceNames.contains(rr.getConferenceName())) {
                conferenceNames.add(rr.getConferenceName());
            }
        }

        List<SubmissionRecord> conferencesFromSubmissionRecord = submissionRecordRepository.findByDataSetEquals(dataSet);
        for (SubmissionRecord sr: conferencesFromSubmissionRecord) {
            if (!conferenceNames.contains(sr.getConferenceName())) {
                conferenceNames.add(sr.getConferenceName());
            }
        }

        return conferenceNames;
    }
}
