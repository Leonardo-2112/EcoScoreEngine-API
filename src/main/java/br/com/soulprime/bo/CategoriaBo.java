package br.com.soulprime.bo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.soulprime.dao.CategoriaAcaoDao;
import br.com.soulprime.exception.ConflitoException;
import br.com.soulprime.exception.EntidadeNaoEncontradaException;
import br.com.soulprime.model.CategoriaAcao;
import br.com.soulprime.model.enums.Perfil;

//Categorias de acao: leitura publica, escrita pelo perfil VALIDADOR
@Service
public class CategoriaBo {

    private final CategoriaAcaoDao categoriaDao;
    private final AcessoBo acessoBo;

    public CategoriaBo(CategoriaAcaoDao categoriaDao, AcessoBo acessoBo) {
        this.categoriaDao = categoriaDao;
        this.acessoBo = acessoBo;
    }

    public List<CategoriaAcao> listar() throws SQLException {
        return categoriaDao.listar();
    }

    public CategoriaAcao buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        return categoriaDao.buscarPorId(id);
    }

    public CategoriaAcao cadastrar(Integer usuarioLogado, CategoriaAcao categoria) throws SQLException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        if (categoriaDao.existeNome(categoria.getNome(), 0)) {
            throw new ConflitoException("Ja existe uma categoria com esse nome.");
        }
        categoria.setNome(ValidadorDados.texto(categoria.getNome()));
        categoria.setDescricao(ValidadorDados.texto(categoria.getDescricao()));
        categoriaDao.cadastrar(categoria);
        return categoria;
    }

    public CategoriaAcao atualizar(Integer usuarioLogado, CategoriaAcao categoria) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        categoriaDao.buscarPorId(categoria.getId());
        if (categoriaDao.existeNome(categoria.getNome(), categoria.getId())) {
            throw new ConflitoException("Ja existe uma categoria com esse nome.");
        }
        categoria.setNome(ValidadorDados.texto(categoria.getNome()));
        categoria.setDescricao(ValidadorDados.texto(categoria.getDescricao()));
        categoriaDao.atualizar(categoria);
        return categoria;
    }

    //Exclusao fisica, permitida apenas para categoria sem acoes vinculadas
    public void remover(Integer usuarioLogado, int id) throws SQLException, EntidadeNaoEncontradaException {
        acessoBo.exigirPerfil(usuarioLogado, Perfil.VALIDADOR);
        categoriaDao.buscarPorId(id);
        if (categoriaDao.contarAcoes(id) > 0) {
            throw new ConflitoException("Categoria possui acoes vinculadas e nao pode ser excluida.");
        }
        categoriaDao.remover(id);
    }
}
