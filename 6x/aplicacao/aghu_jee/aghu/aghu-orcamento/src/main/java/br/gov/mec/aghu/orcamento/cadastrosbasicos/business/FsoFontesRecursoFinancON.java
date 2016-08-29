package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.FsoFontesRecursoFinanc;
import br.gov.mec.aghu.model.FsoFontesXVerbaGestao;
import br.gov.mec.aghu.orcamento.dao.FsoFontesRecursoFinancDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FsoFontesRecursoFinancON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(FsoFontesRecursoFinancON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FsoFontesRecursoFinancDAO fsoFontesRecursoFinancDAO;

	/**
	 * 
	 */	
	private static final long serialVersionUID = -7160595457653656517L;

	public enum FsoFontesRecursoFinancONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_FONTES_RECURSO_FINANC_M03, 
		MENSAGEM_FONTES_RECURSO_FINANC_M05, 
		MENSAGEM_FONTES_RECURSO_FINANC_M07,
		MENSAGEM_FONTES_RECURSO_FINANC_M08
	}

	public Long countPesquisaFontesRecursoFinanc(
			FsoFontesRecursoFinanc fontesRecursoFinanc) {

		return this.getFsoFontesRecursoFinancDAO()
				.countPesquisaFontesRecursoFinanc(fontesRecursoFinanc);
	}

	public FsoFontesRecursoFinanc obterFontesRecursoFinanc(Long codigoFonte) {
		return this.getFsoFontesRecursoFinancDAO().obterPorChavePrimaria(
				codigoFonte);
	}

	public List<FsoFontesRecursoFinanc> listaPesquisaFontesRecursoFinanc(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, FsoFontesRecursoFinanc fontesRecursoFinanc) {

		return this.getFsoFontesRecursoFinancDAO()
				.listaPesquisaFontesRecursoFinanc(firstResult, maxResults,
						orderProperty, asc, fontesRecursoFinanc);

	}

	public boolean verificarFonteRecursoFinancUsadaEmVerbaGestao(FsoFontesRecursoFinanc fontesRecursoFinanc) {
		return this.getFsoFontesRecursoFinancDAO()
				.verificarFonteRecursoFinancUsadaEmVerbaGestao(
						fontesRecursoFinanc);
	}

	public void incluirFontesRecursoFinanc(FsoFontesRecursoFinanc fontesRecursoFinanc)
			throws ApplicationBusinessException {
		// valida cÃ³digo da fonte

		if (fontesRecursoFinanc.getCodigo() <= 0) {
			throw new ApplicationBusinessException(
					FsoFontesRecursoFinancONExceptionCode.MENSAGEM_FONTES_RECURSO_FINANC_M07);
		}

		if (this.getFsoFontesRecursoFinancDAO().verificarFonteRecursoDuplicada(
				fontesRecursoFinanc)) {
			throw new ApplicationBusinessException(
					FsoFontesRecursoFinancONExceptionCode.MENSAGEM_FONTES_RECURSO_FINANC_M03);
		}

		if (fontesRecursoFinanc.getCodigo().toString().length() != 10) {
			throw new ApplicationBusinessException(
					FsoFontesRecursoFinancONExceptionCode.MENSAGEM_FONTES_RECURSO_FINANC_M08);
		}
		
		this.getFsoFontesRecursoFinancDAO().persistir(fontesRecursoFinanc);
	}

	public void alterarFontesRecursoFinanc(FsoFontesRecursoFinanc fontesRecursoFinanc) throws ApplicationBusinessException {
		this.getFsoFontesRecursoFinancDAO().merge(fontesRecursoFinanc);
	}

	public void excluirFontesRecursoFinanc(final Long codigo) throws ApplicationBusinessException {
		final FsoFontesRecursoFinanc fontesRecursoFinanc = fsoFontesRecursoFinancDAO.obterPorChavePrimaria(codigo);
		
		if (fontesRecursoFinanc == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}

		List<FsoFontesXVerbaGestao> listaFontesVerbas =  fsoFontesRecursoFinancDAO.pesquisarFonteRecursoFinancUsadaVerbaGestao(fontesRecursoFinanc);
		
		if (listaFontesVerbas != null && listaFontesVerbas.size() > 0) {
			StringBuilder msg = new StringBuilder();
			
			for (FsoFontesXVerbaGestao verbas : listaFontesVerbas) {
				msg.append(verbas.getVerbaGestao().getSeq()).append(',');	
			}

			throw new ApplicationBusinessException(FsoFontesRecursoFinancONExceptionCode.MENSAGEM_FONTES_RECURSO_FINANC_M05, 
														msg.toString().substring(0, msg.toString().length()-1));
		}

		this.getFsoFontesRecursoFinancDAO().remover(fontesRecursoFinanc);
	}

	private FsoFontesRecursoFinancDAO getFsoFontesRecursoFinancDAO() {
		return fsoFontesRecursoFinancDAO;
	}
}