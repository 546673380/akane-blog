package github.akanemiku.akaneblog.service.impl;

import github.akanemiku.akaneblog.model.Option;
import github.akanemiku.akaneblog.repository.OptionRepository;
import github.akanemiku.akaneblog.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class OptionServiceImpl implements OptionService {

    @Autowired
    private OptionRepository optionRepository;

    @Override
    @Cacheable(value = "options", key = "'allOptions'")
    public List<Option> getOptions() {
        return optionRepository.findAll();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"options", "option"}, allEntries = true, beforeInvocation = true)
    public void saveOptions(Map<String, String> options) {
        if (null != options && !options.isEmpty()) {
            Option option = new Option();
            options.forEach((k,v)->{
                option.setName(k);
                option.setValue(v);
                optionRepository.save(option);
            });
        }
    }

    @Override
    @Cacheable(value = "option", key = "'optionByname+' + #p0")
    public Option getByName(String name) {
        return optionRepository.findById(name).get();
    }
}
