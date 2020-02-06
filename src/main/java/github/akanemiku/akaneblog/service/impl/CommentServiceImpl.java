package github.akanemiku.akaneblog.service.impl;

import github.akanemiku.akaneblog.enums.CommentEnum;
import github.akanemiku.akaneblog.enums.ErrorEnum;
import github.akanemiku.akaneblog.exception.InternalException;
import github.akanemiku.akaneblog.model.Comment;
import github.akanemiku.akaneblog.model.Content;
import github.akanemiku.akaneblog.repository.CommentRepository;
import github.akanemiku.akaneblog.repository.ContentRepository;
import github.akanemiku.akaneblog.service.CommentService;
import github.akanemiku.akaneblog.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ContentRepository contentRepository;

    @Override
    public List<Comment> getCommentsByCid(Integer cid) {
        // TODO cid为空异常
        return commentRepository.findAllByCid(cid);
    }

    @Override
    public void insertComment(Comment comment) {

        // TODO 内容判断
        // TODO 可设置作者，需要后端管理联动
        // 插入评论
        comment.setStatus(CommentEnum.UNCHECKED.getType());
        comment.setCreated(DateUtil.getCurrentUnixTime());
        commentRepository.save(comment);
        // 文章评论总数+1
        Content article = contentRepository.findById(comment.getCid()).get();
        article.setCommentsNum(article.getCommentsNum()+1);
        contentRepository.save(article);
    }

    @Override
    public Page<Comment> getAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    @Override
    public Comment getCommentById(Integer id) {
        return commentRepository.findById(id).get();
    }

    @Override
    @Transactional
    public void updateComment(Integer coid, String status) {
        if (null == coid)
            throw new InternalException(ErrorEnum.PARAM_IS_EMPTY);
        commentRepository.updateCommentStatus(coid, status);
    }
}
