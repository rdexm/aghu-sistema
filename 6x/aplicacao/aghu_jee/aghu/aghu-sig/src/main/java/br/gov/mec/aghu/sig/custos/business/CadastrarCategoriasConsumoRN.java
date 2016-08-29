package br.gov.mec.aghu.sig.custos.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.sig.dao.SigCategoriaConsumosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class CadastrarCategoriasConsumoRN extends BaseBusiness {

	private static final long serialVersionUID = 8182816025946023923L;
	private static final Log LOG = LogFactory.getLog(CadastrarCategoriasConsumoRN.class);
	
	@Inject
	private SigCategoriaConsumosDAO sigCategoriaConsumosDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public enum CadastrarCategoriasConsumoRNExceptionCode implements BusinessExceptionCode {
		ERRO_CATEGORIA_CONSUMO_JA_CADASTRADA, ERRO_CONTAGEM_CONSUMO_JA_CADASTRADA, ERRO_ORDEM_VISUALIZACAO_REPETIDA;
	}

	public void atualizaCategoriaConsumo(SigCategoriaConsumos categoriaConsumo) throws ApplicationBusinessException {
		validaOrdemVisualizacaoCategoriaConsumo(categoriaConsumo);
		if (verificaCategoriaConsumoCadastradaAlteracao(categoriaConsumo)) {
			throw new ApplicationBusinessException(CadastrarCategoriasConsumoRNExceptionCode.ERRO_CATEGORIA_CONSUMO_JA_CADASTRADA,
					categoriaConsumo.getDescricao());
		} else {
			getSigCategoriaConsumosDAO().atualizar(categoriaConsumo);
		}
	}

	public void insereCategoriaConsumo(SigCategoriaConsumos categoriaConsumo, RapServidores servidor) throws ApplicationBusinessException {
		validaOrdemVisualizacaoCategoriaConsumo(categoriaConsumo);
		if (verificaCategoriaConsumoCadastrada(categoriaConsumo)) {
			throw new ApplicationBusinessException(CadastrarCategoriasConsumoRNExceptionCode.ERRO_CATEGORIA_CONSUMO_JA_CADASTRADA,
					categoriaConsumo.getDescricao());
		} else {
			avaliaContagemVinculada(categoriaConsumo, servidor);
		}
	}

	private void avaliaContagemVinculada(SigCategoriaConsumos categoriaConsumo, RapServidores servidor) throws ApplicationBusinessException {
		if (verificaContagemVinculadaCategoria(categoriaConsumo)) {
			throw new ApplicationBusinessException(CadastrarCategoriasConsumoRNExceptionCode.ERRO_CONTAGEM_CONSUMO_JA_CADASTRADA,
					categoriaConsumo.getDescricao());
		} else {
			preparaCategoriaConsumoParaInsercao(categoriaConsumo, servidor);
			getSigCategoriaConsumosDAO().persistir(categoriaConsumo);
		}
	}
	
	private void validaOrdemVisualizacaoCategoriaConsumo(SigCategoriaConsumos categoriaConsumo) throws ApplicationBusinessException {
		if(getSigCategoriaConsumosDAO().validaOrdemVisualizacaoCategoriaConsumo(categoriaConsumo)) {
			throw new ApplicationBusinessException(CadastrarCategoriasConsumoRNExceptionCode.ERRO_ORDEM_VISUALIZACAO_REPETIDA);
		}
	}

	private void preparaCategoriaConsumoParaInsercao(SigCategoriaConsumos categoriaConsumo, RapServidores servidor) {
		categoriaConsumo.setCriadoEm(new Date());
		categoriaConsumo.setServidor(servidor);
	}

	private boolean verificaContagemVinculadaCategoria(SigCategoriaConsumos categoriaConsumo) {
		return getSigCategoriaConsumosDAO().verificaContagemVinculadaCategoria(categoriaConsumo);
	}

	private boolean verificaCategoriaConsumoCadastrada(SigCategoriaConsumos categoriaConsumo) {
		return getSigCategoriaConsumosDAO().verificaCategoriaConsumoCadastrada(categoriaConsumo);
	}

	private boolean verificaCategoriaConsumoCadastradaAlteracao(SigCategoriaConsumos categoriaConsumo) {
		return getSigCategoriaConsumosDAO().verificaCategoriaConsumoCadastradaAlteracao(categoriaConsumo);
	}

	protected SigCategoriaConsumosDAO getSigCategoriaConsumosDAO() {
		return this.sigCategoriaConsumosDAO;
	}

}
