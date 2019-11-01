package sg.edu.nus.comp.cs3219.viz.logic;

import org.springframework.stereotype.Component;
import sg.edu.nus.comp.cs3219.viz.common.entity.Presentation;
import sg.edu.nus.comp.cs3219.viz.common.entity.PresentationSection;
import sg.edu.nus.comp.cs3219.viz.storage.repository.PresentationSectionRepository;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;
import java.util.Collections;

@Component
public class PresentationSectionLogic {

    private PresentationSectionRepository presentationSectionRepository;

    public PresentationSectionLogic(PresentationSectionRepository presentationSectionRepository) {
        this.presentationSectionRepository = presentationSectionRepository;
    }

    public List<PresentationSection> findAllByPresentation(Presentation presentation) {
        return presentationSectionRepository.findAllByPresentation(presentation);
    }

    public PresentationSection saveForPresentation(Presentation presentation, PresentationSection presentationSection) {
        PresentationSection newPresentationSection = new PresentationSection();

        newPresentationSection.setPresentation(presentation);
        newPresentationSection.setTitle(presentationSection.getTitle());
        newPresentationSection.setDescription(presentationSection.getDescription());
        newPresentationSection.setType(presentationSection.getType());
        newPresentationSection.setDataSet(presentationSection.getDataSet());
        newPresentationSection.setConferenceName(presentationSection.getConferenceName());
        newPresentationSection.setSelections(presentationSection.getSelections());
        newPresentationSection.setInvolvedRecords(presentationSection.getInvolvedRecords());
        newPresentationSection.setFilters(presentationSection.getFilters());
        newPresentationSection.setJoiners(presentationSection.getJoiners());
        newPresentationSection.setGroupers(presentationSection.getGroupers());
        newPresentationSection.setSectionIndex(presentationSection.getSectionIndex());
        newPresentationSection.setSorters(presentationSection.getSorters());
        newPresentationSection.setExtraData(presentationSection.getExtraData());

        return presentationSectionRepository.save(newPresentationSection);
    }

    public Optional<PresentationSection> findById(Long id) {
        return presentationSectionRepository.findById(id);
    }

    public PresentationSection updatePresentation(PresentationSection oldPresentationSection, PresentationSection newPresentationSection) {
        oldPresentationSection.setTitle(newPresentationSection.getTitle());
        oldPresentationSection.setDescription(newPresentationSection.getDescription());
        oldPresentationSection.setDataSet(newPresentationSection.getDataSet());
        oldPresentationSection.setConferenceName(newPresentationSection.getConferenceName());
        oldPresentationSection.setSelections(newPresentationSection.getSelections());
        oldPresentationSection.setInvolvedRecords(newPresentationSection.getInvolvedRecords());
        oldPresentationSection.setFilters(newPresentationSection.getFilters());
        oldPresentationSection.setJoiners(newPresentationSection.getJoiners());
        oldPresentationSection.setGroupers(newPresentationSection.getGroupers());
        oldPresentationSection.setSectionIndex(newPresentationSection.getSectionIndex());
        oldPresentationSection.setSorters(newPresentationSection.getSorters());
        oldPresentationSection.setExtraData(newPresentationSection.getExtraData());

        return presentationSectionRepository.save(oldPresentationSection);
    }

    public PresentationSection[] swapSectionIndices(PresentationSection presentationSectionOne, PresentationSection presentationSectionTwo) {
        Integer indexToSwap = presentationSectionOne.getSectionIndex();
        presentationSectionOne.setSectionIndex(presentationSectionTwo.getSectionIndex());
        presentationSectionTwo.setSectionIndex(indexToSwap);
    
        PresentationSection[] returnArray = {presentationSectionRepository.save(presentationSectionOne),
                                             presentationSectionRepository.save(presentationSectionTwo)};

        return returnArray;
    }

    public List<PresentationSection> deleteById(Long id, Presentation presentation) {
        presentationSectionRepository.deleteById(id);

        // Update all the sectionIndices, so that when the section is removed
        // on the frontEnd, the sectionIndices for remaining sections will be correct.
        List<PresentationSection> allSections = this.findAllByPresentation(presentation);
        Collections.sort(allSections, Comparator.comparing(section -> section.getSectionIndex()));
        for (int i = 0; i < allSections.size(); i++) {
            allSections.get(i).setSectionIndex(i);
        }
        return allSections;
    }
}
