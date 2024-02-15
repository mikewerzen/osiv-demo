package com.demo.osivdemo.service;

import com.demo.osivdemo.dto.ParentEntityDto;
import com.demo.osivdemo.util.EntityMapper;
import com.demo.osivdemo.util.SessionUtils;
import com.demo.osivdemo.domain.ChildEntity;
import com.demo.osivdemo.domain.GrandchildEntity;
import com.demo.osivdemo.domain.ParentEntity;
import com.demo.osivdemo.repository.ParentEntityRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
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

    public ParentEntity getSingleParentInOSIV() {
        logger.info("Get single parent in OSIV!");
        sessionUtils.logActiveConnections();
        ParentEntity parent = parentEntityRepository.findById(1L).get();
        logger.info("Retrieved Parent");
        sessionUtils.logActiveConnections();

        demoExternalService.pretendToMakeRestCall();

        return parent;
    }

    public ParentEntity getSingleParentWithSeparateSession() {
        logger.info("Get single parent with separate session!");
        sessionUtils.logActiveConnections();
        Session separateSession = sessionUtils.createNewSession();
        ParentEntity parent = separateSession.get(ParentEntity.class, 1L);
        logger.info("Using separate session");
        sessionUtils.logActiveConnections();
        parent.getChildEntities().size();
        separateSession.close();
        logger.info("Done with separate session");
        sessionUtils.logActiveConnections();

        demoExternalService.pretendToMakeRestCall();

        return parent;
    }

    public ParentEntity getSingleParentInAsyncSession() {
        logger.info("Get single parent in AsyncSession!");
        sessionUtils.logActiveConnections();
        ParentEntity parent = sessionUtils.executeInAsyncSession(() -> {
            logger.info("Inside Async Session!");
            ParentEntity parentEntity = parentEntityRepository.findById(1L).get();
            sessionUtils.logActiveConnections();
            return parentEntity;
        });
        logger.info("Retrieved Parent");
        sessionUtils.logActiveConnections();

        demoExternalService.pretendToMakeRestCall();

        logger.info("Merging in parent");
        parent = parentEntityRepository.save(parent);
        return parent;
    }

    public ParentEntity saveSingleParent() {
        ParentEntity parentEntity = new ParentEntity();
        parentEntity.setName("Single Parent");
        parentEntity.setDescription("This parent doesn't have children");
        parentEntityRepository.save(parentEntity);
        return parentEntity;
    }

    public Optional<ParentEntity> getFamily() {
        logger.info("Getting Family!");
        return parentEntityRepository.findById(1L);
    }

    @Transactional
    public ParentEntity getFamilyNoOSIVHacky() {
        logger.info("Getting Family in Non-OSIV Environment");
        ParentEntity parent = parentEntityRepository.findById(1L).get();
        for(ChildEntity childEntity : parent.getChildEntities()) {
            childEntity.getGrandchildEntities().size();
        }
        return parent;
    }

    @Transactional
    public ParentEntityDto getFamilyNoOSIV() {
        logger.info("Getting Family in Non-OSIV Environment");
        ParentEntity parent = parentEntityRepository.findById(1L).get();
        return EntityMapper.mapEntityToDTO(parent);
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
