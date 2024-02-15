package com.demo.osivdemo.service;

import com.demo.osivdemo.SessionUtils;
import com.demo.osivdemo.domain.ChildEntity;
import com.demo.osivdemo.domain.GrandchildEntity;
import com.demo.osivdemo.domain.ParentEntity;
import com.demo.osivdemo.repository.ParentEntityRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DemoService {

    private static final Logger logger = LoggerFactory.getLogger(DemoService.class);

    private final DemoExternalService demoExternalService;
    private final SessionUtils sessionUtils;
    private final ParentEntityRepository parentEntityRepository;

    public DemoService(DemoExternalService demoExternalService, SessionUtils sessionUtils,
                       ParentEntityRepository parentEntityRepository) {
        this.demoExternalService = demoExternalService;
        this.sessionUtils = sessionUtils;
        this.parentEntityRepository = parentEntityRepository;
    }

    public void doSomethingNontransactional() {
        logger.info("Doing Something Non-Transactional!");
        sessionUtils.logActiveConnections();
        demoExternalService.pretendToMakeRestCall();
        sessionUtils.logActiveConnections();
    }

    @Transactional
    public void doSomethingTransactional() {
        logger.info("Doing Something Transactional!");
        sessionUtils.logActiveConnections();
        demoExternalService.pretendToMakeRestCall();
        sessionUtils.logActiveConnections();
    }

    public Optional<ParentEntity> getFamily() {
        logger.info("Getting Family!");
        return parentEntityRepository.findById(1L);
    }

    public ParentEntity saveFamily() {
        logger.info("Saving Family!");
        ParentEntity parent = createFamily("Smith", 2, 3);
        return parentEntityRepository.save(parent);
    }

    public ParentEntity saveMegaFamily() {
        logger.info("Saving Mega Family!");
        ParentEntity parent = createFamily("Smith", 30, 40);
        return parentEntityRepository.save(parent);
    }


    private ParentEntity createFamily(String familyLastName, int numChildren, int numGrandchildrenPerChild) {
        ParentEntity parentEntity = new ParentEntity();
        parentEntity.setName("Parent-" + familyLastName);
        parentEntity.setDescription("Parent " + familyLastName);

        for (int i = 0; i < numChildren; i++) {
            ChildEntity childEntity = new ChildEntity();
            childEntity.setName("Child-" + familyLastName + "-" + i);
            childEntity.setDescription("Child-" + i + " of " + parentEntity.getDescription());
            parentEntity.getChildEntities().add(childEntity);
            childEntity.setParentEntity(parentEntity);

            for (int j = 0; j < numGrandchildrenPerChild; j++) {
                GrandchildEntity grandchildEntity = new GrandchildEntity();
                grandchildEntity.setName("GrandChild-" + familyLastName + "-" + i + "-" + j);
                grandchildEntity.setDescription("GrandChild-" + j + " of " + childEntity.getDescription());
                grandchildEntity.setChildEntity(childEntity);
                childEntity.getGrandchildEntities().add(grandchildEntity);
            }
        }

        return  parentEntity;
    }

}
