package br.gov.mec.aghu.compras.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoHistoricoAdvertFornDAO;
import br.gov.mec.aghu.compras.dao.ScoHistoricoMultaFornDAO;
import br.gov.mec.aghu.compras.dao.ScoHistoricoOcorrFornDAO;
import br.gov.mec.aghu.compras.dao.ScoHistoricoSuspensFornDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoHistoricoAdvertForn;
import br.gov.mec.aghu.model.ScoHistoricoAdvertFornId;
import br.gov.mec.aghu.model.ScoHistoricoMultaForn;
import br.gov.mec.aghu.model.ScoHistoricoMultaFornId;
import br.gov.mec.aghu.model.ScoHistoricoOcorrForn;
import br.gov.mec.aghu.model.ScoHistoricoOcorrFornId;
import br.gov.mec.aghu.model.ScoHistoricoSuspensForn;
import br.gov.mec.aghu.model.ScoHistoricoSuspensFornId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ManterPenalidadeFornecedorON extends BaseBusiness {
	
	@Inject
	private ScoHistoricoAdvertFornDAO scoHistoricoAdvertFornDAO;
	
	@Inject
	private ScoHistoricoMultaFornDAO scoHistoricoMultaFornDAO;
	
	@Inject
	private ScoHistoricoSuspensFornDAO scoHistoricoSuspensFornDAO;
	
	@Inject
	private ScoHistoricoOcorrFornDAO scoHistoricoOcorrFornDAO;

	private static final Log LOG = LogFactory.getLog(ManterPenalidadeFornecedorON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7620204295459637684L;

	public enum ManterPenalidadeFornecedorONExceptionCode implements BusinessExceptionCode { 
		FORNECEDOR_OU_PERIODO_OBRIGATORIO, PERIODO_DATA_FINAL_INFERIOR_DATA_INICIAL, PERIODO_DATA_INICIAL_INVALIDO,
		DATA_MULTA_ADV_OCORR_MAIOR_DATA_ATUAL, DATA_FINAL_SUSPENSAO_INFERIOR_DATA_INICAL, DATA_FINAL_SUSPENSAO_INFERIOR_DATA_ATUAL,
		ERRO_SOBREPOSICAO_SUSPENSAO;  
	}

	
	public List<ScoHistoricoAdvertForn> pesquisarAdvertenciasFornecedor(Integer numero, Date inicio, Date fim) throws BaseException {
		fim = this.validarPesquisa(numero, inicio, fim);
		
		return scoHistoricoAdvertFornDAO.listarAdvertenciasPorFornecedorOuPeriodo(numero, inicio, fim);
	}

	public List<ScoHistoricoMultaForn> pesquisarMultasFornecedor(Integer numero, Date inicio, Date fim) throws BaseException {
		fim = this.validarPesquisa(numero, inicio, fim);
		
		return scoHistoricoMultaFornDAO.listarMultasPorFornecedorOuPeriodo(numero, inicio, fim);
	}

	public List<ScoHistoricoSuspensForn> pesquisarSuspensoesFornecedor(Integer numero, Date inicio, Date fim) throws BaseException {
		fim = this.validarPesquisa(numero, inicio, fim);
		
		return scoHistoricoSuspensFornDAO.listarSuspensoesPorFornecedorOuPeriodo(numero, inicio, fim);
	}

	public List<ScoHistoricoOcorrForn> pesquisarOcorrenciasFornecedor(Integer numero, Date inicio, Date fim) throws BaseException {
		fim = this.validarPesquisa(numero, inicio, fim);
		
		return scoHistoricoOcorrFornDAO.listarOcorrenciasPorFornecedorOuPeriodo(numero, inicio, fim);
	}
	
	private Date validarPesquisa(Integer numero, Date inicio, Date fim) throws BaseException {
		validarPesquisaFornecedorOuPeriodoObrigatorio(numero, inicio, fim);
		return this.validarPeriodo(numero == null, inicio, fim);
	}
	
	private void validarPesquisaFornecedorOuPeriodoObrigatorio(Integer numero, Date inicio, Date fim) throws BaseException {
		if(!(numero != null || inicio != null || fim != null)) {
			throw new ApplicationBusinessException(ManterPenalidadeFornecedorONExceptionCode.FORNECEDOR_OU_PERIODO_OBRIGATORIO);
		}
	}
	
	private Date validarPeriodo(Boolean obrigatorio, Date inicio, Date fim) throws BaseException {
		if(inicio != null && fim != null) {
			if(DateUtil.validaDataTruncadaMaior(inicio, fim)) {
				throw new ApplicationBusinessException(ManterPenalidadeFornecedorONExceptionCode.PERIODO_DATA_FINAL_INFERIOR_DATA_INICIAL);
			}
		}
		else {
			if(fim != null) {
				throw new ApplicationBusinessException(ManterPenalidadeFornecedorONExceptionCode.PERIODO_DATA_INICIAL_INVALIDO);
			}
			else if(obrigatorio) {
				return new Date();
			}
		}
		
		return fim;
	}
	
	private void validarDataMultaOuAdvertenciaOuOcorrencia(Date data) throws BaseException {
		if(DateUtil.validaDataTruncadaMaior(data, new Date())) {
			throw new ApplicationBusinessException(ManterPenalidadeFornecedorONExceptionCode.DATA_MULTA_ADV_OCORR_MAIOR_DATA_ATUAL);
		}
	}
	
	private void validarDataFinalSuspensao(Date inicio, Date fim) throws BaseException {
		if(fim != null) {
			if(DateUtil.validaDataTruncadaMaior(inicio, fim)) {
				throw new ApplicationBusinessException(ManterPenalidadeFornecedorONExceptionCode.DATA_FINAL_SUSPENSAO_INFERIOR_DATA_INICAL);
			}
			if(DateUtil.validaDataMenor(DateUtil.truncaData(fim), DateUtil.truncaData(new Date()))) {
				throw new ApplicationBusinessException(ManterPenalidadeFornecedorONExceptionCode.DATA_FINAL_SUSPENSAO_INFERIOR_DATA_ATUAL);
			}
		}
	}

	public void persistirMulta(ScoHistoricoMultaForn multa) throws BaseException {
		validarDataMultaOuAdvertenciaOuOcorrencia(multa.getDtEmissao());
		if(multa.getId().getNumero() == null) {
			multa.getId().setNumero(scoHistoricoMultaFornDAO.obterNumeroMaximoPeloFornecedor(multa.getId().getFrnNumero()));
			scoHistoricoMultaFornDAO.persistir(multa);
		} else {
			scoHistoricoMultaFornDAO.atualizar(multa);
		}
	}

	public void persistirAdvertencia(ScoHistoricoAdvertForn advertencia) throws BaseException {
		validarDataMultaOuAdvertenciaOuOcorrencia(advertencia.getDtEmissao());
		if(advertencia.getId().getNumero() == null) {
			advertencia.getId().setNumero(scoHistoricoAdvertFornDAO.obterNumeroMaximoPeloFornecedor(advertencia.getId().getFrnNumero()));
			scoHistoricoAdvertFornDAO.persistir(advertencia);
		} else {
			scoHistoricoAdvertFornDAO.atualizar(advertencia);
		}
	}

	public void persistirOcorrencia(ScoHistoricoOcorrForn ocorrencia) throws BaseException {
		validarDataMultaOuAdvertenciaOuOcorrencia(ocorrencia.getDtEmissao());
		if(ocorrencia.getId().getNumero() == null) {
			ocorrencia.getId().setNumero(scoHistoricoOcorrFornDAO.obterNumeroMaximoPeloFornecedor(ocorrencia.getId().getFrnNumero()));
			scoHistoricoOcorrFornDAO.persistir(ocorrencia);
		} else {
			scoHistoricoOcorrFornDAO.atualizar(ocorrencia);
		}
	}

	public void persistirSuspensao(ScoHistoricoSuspensForn suspensao, RapServidores servidor) throws BaseException {
		validarDataFinalSuspensao(suspensao.getDtInicio() ,suspensao.getDtFim());
		if(scoHistoricoSuspensFornDAO.isSobreposicaoSuspensao(suspensao.getId().getFrnNumero(), suspensao.getId().getNroProcesso(), suspensao.getDtInicio(), suspensao.getDtFim())) {
			throw new ApplicationBusinessException(ManterPenalidadeFornecedorONExceptionCode.ERRO_SOBREPOSICAO_SUSPENSAO);
		}
		suspensao.setRapServidores(servidor);
		if(suspensao.getId().getNroProcesso() == null) {
			suspensao.getId().setNroProcesso(scoHistoricoSuspensFornDAO.obterNumeroMaximoPeloFornecedor(suspensao.getId().getFrnNumero()));
			scoHistoricoSuspensFornDAO.persistir(suspensao);
		} else {
			scoHistoricoSuspensFornDAO.atualizar(suspensao);
		}
	}
	
	public void removerAdvertencia(ScoHistoricoAdvertFornId advertenciaId) {
		ScoHistoricoAdvertForn advertencia = scoHistoricoAdvertFornDAO.obterPorChavePrimaria(advertenciaId);
		scoHistoricoAdvertFornDAO.remover(advertencia);
	}

	public void removerMulta(ScoHistoricoMultaFornId multaId) {
		ScoHistoricoMultaForn multa = scoHistoricoMultaFornDAO.obterPorChavePrimaria(multaId);
		scoHistoricoMultaFornDAO.remover(multa);
	}

	public void removerOcorrencia(ScoHistoricoOcorrFornId ocorrenciaId) {
		ScoHistoricoOcorrForn ocorrencia = scoHistoricoOcorrFornDAO.obterPorChavePrimaria(ocorrenciaId);
		scoHistoricoOcorrFornDAO.remover(ocorrencia);
	}

	public void removerSuspensao(ScoHistoricoSuspensFornId suspensaoId) {
		ScoHistoricoSuspensForn suspensao = scoHistoricoSuspensFornDAO.obterPorChavePrimaria(suspensaoId);
		scoHistoricoSuspensFornDAO.remover(suspensao);
	}
}
