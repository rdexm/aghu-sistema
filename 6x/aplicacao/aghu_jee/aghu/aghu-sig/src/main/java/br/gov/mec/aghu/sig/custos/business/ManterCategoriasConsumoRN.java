package br.gov.mec.aghu.sig.custos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.sig.dao.SigCalculoAtdConsumoDAO;
import br.gov.mec.aghu.sig.dao.SigCategoriaConsumosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterCategoriasConsumoRN extends BaseBusiness {

	private static final long serialVersionUID = 3709061791627610083L;
	private static final Log LOG = LogFactory.getLog(ManterCategoriasConsumoRN.class);

	@Inject
	private SigCalculoAtdConsumoDAO sigCalculoAtdConsumoDAO;

	@Inject
	private SigCategoriaConsumosDAO sigCategoriaConsumosDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public enum ManterCategoriasConsumoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_EXCLUSAO_CATEGORIA_CONSUMO;
	}

	protected SigCalculoAtdConsumoDAO getSigCalculoAtdConsumoDAO() {
		return this.sigCalculoAtdConsumoDAO;
	}

	protected SigCategoriaConsumosDAO getSigCategoriaConsumosDAO() {
		return this.sigCategoriaConsumosDAO;
	}

	public List<SigCategoriaConsumos> buscaCategoriasDeConsumo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			String descricao, DominioIndContagem indContagem, DominioSituacao situacao) {
		return getSigCategoriaConsumosDAO().buscaCategoriasDeConsumo(firstResult, maxResult, orderProperty, asc, descricao, indContagem,
				situacao);
	}

	public Long buscaCategoriasDeConsumoCount(String descricao, DominioIndContagem indContagem, DominioSituacao situacao) {
		return getSigCategoriaConsumosDAO().buscaCategoriasDeConsumoCount(descricao, indContagem, situacao);
	}

	public void excluirCategoriaConsumo(SigCategoriaConsumos categoriaConsumo) throws ApplicationBusinessException {
		if (getSigCalculoAtdConsumoDAO().verificaConsumosContabilizados(categoriaConsumo)) {
			throw new ApplicationBusinessException(ManterCategoriasConsumoRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_CATEGORIA_CONSUMO);
		} else {
			getSigCategoriaConsumosDAO().removerPorId(categoriaConsumo.getSeq());
		}
	}

}
