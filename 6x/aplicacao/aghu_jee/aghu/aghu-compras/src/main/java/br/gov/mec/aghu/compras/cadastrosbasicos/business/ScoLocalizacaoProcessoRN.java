package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAndamentoProcessoCompraDAO;
import br.gov.mec.aghu.compras.dao.ScoLocalizacaoProcessoDAO;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoLocalizacaoProcessoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ScoLocalizacaoProcessoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@Inject
	private ScoAndamentoProcessoCompraDAO scoAndamentoProcessoCompraDAO;
	
	@Inject
	private ScoLocalizacaoProcessoDAO scoLocalizacaoProcessoDAO;

	private static final long serialVersionUID = 9167224511640571507L;

	public enum ScoLocalizacaoProcessoRNExceptionCode implements
			BusinessExceptionCode { MENSAGEM_PARAM_OBRIG, MENSAGEM_LOC_PROCESSO_RN1, MENSAGEM_LOC_PROCESSO_RN2, MENSAGEM_LOC_PROCESSO_RN3;
	}

	/**
	 * Insere local do processo pelo código
	 * @ORADB - SCOP_ENFORCE_LCP_RULES
	 * @param ScoLocalizacaoProcesso
	 * @throws ApplicationBusinessException 
	 */
	public void persistir(ScoLocalizacaoProcesso scoLocalizacaoProcesso) throws ApplicationBusinessException {

		if (scoLocalizacaoProcesso == null) {
			throw new ApplicationBusinessException(ScoLocalizacaoProcessoRNExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		// RN1 - Verifica se existe algum registro com indArquivoMorto(true)
		if (scoLocalizacaoProcesso.getIndArquivoMorto()) {
			ScoLocalizacaoProcesso paramResult = this.getScoLocalizacaoProcessoDAO()
					.obterLocalizacaoProcessoPorArquivoMorto(scoLocalizacaoProcesso.getIndArquivoMorto());
			if (paramResult != null && paramResult.getCodigo() != null) {
					throw new ApplicationBusinessException(ScoLocalizacaoProcessoRNExceptionCode.MENSAGEM_LOC_PROCESSO_RN1);
			}		
		}
		
		// RN2 - Verifica se existe algum registro com indLocaloriginario(true), para o servidorResponsavel informado
		if (scoLocalizacaoProcesso.getIndLocalOriginario()) {
			ScoLocalizacaoProcesso paramResult = this.getScoLocalizacaoProcessoDAO()
					.obterLocalizacaoProcessoPorResponsavelComLocalOriginario(scoLocalizacaoProcesso.getServidorResponsavel());
			if (paramResult != null && paramResult.getCodigo() != null) {
					throw new ApplicationBusinessException(ScoLocalizacaoProcessoRNExceptionCode.MENSAGEM_LOC_PROCESSO_RN2);
			}		
		}

		this.getScoLocalizacaoProcessoDAO().persistir(scoLocalizacaoProcesso);
	}

	/**
	 * Altera local do processo pelo código
	 * @ORADB - SCOP_ENFORCE_LCP_RULES
	 */
	public void atualizar(ScoLocalizacaoProcesso scoLocalizacaoProcesso) throws ApplicationBusinessException {

		if (scoLocalizacaoProcesso == null) {
			throw new ApplicationBusinessException(ScoLocalizacaoProcessoRNExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		// RN1 - Verifica se existe algum registro com indArquivoMorto(true)
		if (scoLocalizacaoProcesso.getIndArquivoMorto()) {
			ScoLocalizacaoProcesso paramResult = this.getScoLocalizacaoProcessoDAO()
					.obterLocalizacaoProcessoPorArquivoMorto(scoLocalizacaoProcesso.getIndArquivoMorto());
			if (paramResult != null) {
				if (!paramResult.getCodigo().equals(scoLocalizacaoProcesso.getCodigo())) {
					throw new ApplicationBusinessException(ScoLocalizacaoProcessoRNExceptionCode.MENSAGEM_LOC_PROCESSO_RN1);
				}
			}		
		}
		
		// RN2 - Verifica se existe algum registro com indLocaloriginario(true), para o servidorResponsavel informado
		if (scoLocalizacaoProcesso.getIndLocalOriginario()) {
			ScoLocalizacaoProcesso paramResult = this.getScoLocalizacaoProcessoDAO()
					.obterLocalizacaoProcessoPorResponsavelComLocalOriginario(scoLocalizacaoProcesso.getServidorResponsavel());
			if (paramResult != null) {
				if (!paramResult.getCodigo().equals(scoLocalizacaoProcesso.getCodigo())) {
					throw new ApplicationBusinessException(ScoLocalizacaoProcessoRNExceptionCode.MENSAGEM_LOC_PROCESSO_RN2);
				}
			}		
		}

		this.getScoLocalizacaoProcessoDAO().merge(scoLocalizacaoProcesso);
	}

	/**
	 * Exclui local do processo pelo código
	 */
	public void remover(Short codigo) throws ApplicationBusinessException {

		//RN3 - Verifica se existe algum registro na SCO_ANDAMENTO_PROCESSO_COMPRAS antes da exclusão
		Long qtdeReg = this.getScoAndamentoProcessoCompraDAO().obterQuantidadeRegistrosPeloCodigoLocProc(codigo);
		
		if (qtdeReg > 0) {
			throw new ApplicationBusinessException(ScoLocalizacaoProcessoRNExceptionCode.MENSAGEM_LOC_PROCESSO_RN3);
		}		

		this.getScoLocalizacaoProcessoDAO().removerPorId(codigo);
	}
	
	private ScoLocalizacaoProcessoDAO getScoLocalizacaoProcessoDAO() {
		return scoLocalizacaoProcessoDAO;
	}
	
	private ScoAndamentoProcessoCompraDAO getScoAndamentoProcessoCompraDAO() {
		return scoAndamentoProcessoCompraDAO;
	}
}