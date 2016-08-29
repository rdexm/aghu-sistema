package br.gov.mec.aghu.compras.solicitacaocompra.business;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoDireitoAutorizacaoTempDAO;
import br.gov.mec.aghu.compras.dao.ScoPontoServidorDAO;
import br.gov.mec.aghu.compras.vo.ItensScoDireitoAutorizacaoTempVO;
import br.gov.mec.aghu.compras.vo.ItensScoDireitoAutorizacaoTempVO.Operacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoDireitoAutorizacaoTemp;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AutorizacoesTemporariasParaGeracaoScON extends BaseBusiness {

    private static final Log LOG = LogFactory.getLog(AutorizacoesTemporariasParaGeracaoScON.class);

    @Override
    @Deprecated
    protected Log getLogger() {
	return LOG;
    }

    @Inject
    private ScoPontoServidorDAO scoPontoServidorDAO;

    @Inject
    private ScoDireitoAutorizacaoTempDAO scoDireitoAutorizacaoTempDAO;

    /**
	 * 
	 */
    private static final long serialVersionUID = -7130595457753656917L;

    public enum ScoDireitoAutorizacaoTempONExceptionCode implements BusinessExceptionCode {
	MENSAGEM_PARAM_OBRIG, MENSAGEM_DATA_FIM_MAIOR, MENSAGEM_ITEM_DUPLICADO_DIREITOS_TEMPORARIOS, MENSAGEM_PERIODO_CONFLITANTE, MENSAGEM_DATA_RETROATIVA
    }

    public List<ScoPontoServidor> pesquisarScoPontoServidor(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao, RapServidores servidor,
	    RapServidores servidorAutorizado, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {

	return getScoPontoServidorDAO().pesquisarScoPontoServidor(scoPontoParadaSolicitacao, servidor, servidorAutorizado, firstResult,
		maxResults, orderProperty, asc);

    }

    public Long pesquisarScoPontoServidorCount(ScoPontoParadaSolicitacao scoPontoParadaSolicitacao, RapServidores servidor,
	    RapServidores servidorAutorizado) {

	return getScoPontoServidorDAO().pesquisarScoPontoServidorCount(scoPontoParadaSolicitacao, servidor, servidorAutorizado);

    }

    public List<ScoDireitoAutorizacaoTemp> pesquisarScoDireitoAutorizacaoTemp(ScoPontoServidor pontoServidor, RapServidores servidor,
	    Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {

	return getScoDireitoAutorizacaoTempDAO().pesquisarScoDireitoAutorizacaoTemp(pontoServidor, servidor, firstResult, maxResults,
		orderProperty, asc);

    }

    public Long pesquisarScoDireitoAutorizacaoTempCount(ScoPontoServidor pontoServidor, RapServidores servidor) {

	return getScoDireitoAutorizacaoTempDAO().pesquisarScoDireitoAutorizacaoTempCount(pontoServidor, servidor);

    }

    public List<ScoDireitoAutorizacaoTemp> listarScoDireitoAutorizacaoTemp(ScoPontoServidor pontoServidor, RapServidores servidor) {

	return getScoDireitoAutorizacaoTempDAO().listarScoDireitoAutorizacaoTemp(pontoServidor, servidor);

    }

    public void validaDataInicioFimScoDireitoAutorizacaoTemp(Date dataInicio, Date dataFim) throws ApplicationBusinessException {
	if (DateUtil.truncaData(dataInicio).before(DateUtil.truncaData(new Date()))
		|| DateUtil.truncaData(dataFim).before(DateUtil.truncaData(new Date()))) {
	    throw new ApplicationBusinessException(ScoDireitoAutorizacaoTempONExceptionCode.MENSAGEM_DATA_RETROATIVA);
	}

	if (dataInicio.after(dataFim)) {
	    throw new ApplicationBusinessException(ScoDireitoAutorizacaoTempONExceptionCode.MENSAGEM_DATA_FIM_MAIOR);
	}

    }

    public void mensagemErroDuplicadoScoDireitoAutorizacaoTemp() throws ApplicationBusinessException {
	throw new ApplicationBusinessException(ScoDireitoAutorizacaoTempONExceptionCode.MENSAGEM_ITEM_DUPLICADO_DIREITOS_TEMPORARIOS);
    }

    public void validarConflitoPeriodoDatas(List<ItensScoDireitoAutorizacaoTempVO> listaGrade,
	    ScoDireitoAutorizacaoTemp direitoAutorizacao, Integer idLista) throws ApplicationBusinessException {
	Integer i = 0;
	if (listaGrade != null && !listaGrade.isEmpty() && direitoAutorizacao != null) {
	    for (ItensScoDireitoAutorizacaoTempVO item : listaGrade) {
		if (i.equals(idLista)
			&& Objects.equals(item.getDireitoAutorizacaoTemp().getDtInicio(), direitoAutorizacao.getDtInicio())
			&& Objects.equals(item.getDireitoAutorizacaoTemp().getDtFim(), direitoAutorizacao.getDtFim())) {
		    continue;
		}

		if (!item.getOperacao().equals(Operacao.DELETE)) {
		    if (CoreUtil.isBetweenDatas(direitoAutorizacao.getDtInicio(), item.getDireitoAutorizacaoTemp().getDtInicio(), item
			    .getDireitoAutorizacaoTemp().getDtFim())
			    || CoreUtil.isBetweenDatas(direitoAutorizacao.getDtFim(), item.getDireitoAutorizacaoTemp().getDtInicio(), item
				    .getDireitoAutorizacaoTemp().getDtFim())) {
			if (idLista == null || (idLista != null && !i.equals(idLista))) {
			    throw new ApplicationBusinessException(ScoDireitoAutorizacaoTempONExceptionCode.MENSAGEM_PERIODO_CONFLITANTE);
			}
		    }
		}
		i++;
	    }
	}
    }

    /**
     * alterar ScoDireitoAutorizacaoTemp
     * 
     * @param scoDireitoAutorizacaoTemp
     * @throws ApplicationBusinessException
     */

    public void alterarScoDireitoAutorizacaoTemp(ScoDireitoAutorizacaoTemp scoDireitoAutorizacaoTemp) throws ApplicationBusinessException {

	if (scoDireitoAutorizacaoTemp == null) {
	    throw new ApplicationBusinessException(ScoDireitoAutorizacaoTempONExceptionCode.MENSAGEM_PARAM_OBRIG);
	}

	validaDataInicioFimScoDireitoAutorizacaoTemp(scoDireitoAutorizacaoTemp.getDtInicio(), scoDireitoAutorizacaoTemp.getDtFim());

	if (this.getScoDireitoAutorizacaoTempDAO().isDuplicadoScoDireitoAutorizacaoTemp(scoDireitoAutorizacaoTemp)) {
	    this.mensagemErroDuplicadoScoDireitoAutorizacaoTemp();
	}
	// validaDireitoAutorizacaoTemp(scoDireitoAutorizacaoTemp);

	this.getScoDireitoAutorizacaoTempDAO().merge(scoDireitoAutorizacaoTemp);
    }

    /**
     * Inserir ScoDireitoAutorizacaoTemp
     * 
     * @param scoDireitoAutorizacaoTemp
     * @throws ApplicationBusinessException
     */
    public void inserirScoDireitoAutorizacaoTemp(ScoDireitoAutorizacaoTemp scoDireitoAutorizacaoTemp) throws ApplicationBusinessException {

	if (scoDireitoAutorizacaoTemp == null) {
	    throw new ApplicationBusinessException(ScoDireitoAutorizacaoTempONExceptionCode.MENSAGEM_PARAM_OBRIG);
	}

	validaDataInicioFimScoDireitoAutorizacaoTemp(scoDireitoAutorizacaoTemp.getDtInicio(), scoDireitoAutorizacaoTemp.getDtFim());

	if (this.getScoDireitoAutorizacaoTempDAO().isDuplicadoScoDireitoAutorizacaoTemp(scoDireitoAutorizacaoTemp)) {
	    this.mensagemErroDuplicadoScoDireitoAutorizacaoTemp();
	}

	// validaDireitoAutorizacaoTemp(scoDireitoAutorizacaoTemp);

	this.getScoDireitoAutorizacaoTempDAO().persistir(scoDireitoAutorizacaoTemp);

    }

    /**
     * excluir ScoDireitoAutorizacaoTemp
     */
    public void excluirScoDireitoAutorizacaoTemp(ScoDireitoAutorizacaoTemp scoDireitoAutorizacaoTemp) throws ApplicationBusinessException {

	if (scoDireitoAutorizacaoTemp == null) {
	    throw new ApplicationBusinessException(ScoDireitoAutorizacaoTempONExceptionCode.MENSAGEM_PARAM_OBRIG);
	}

	// validaDireitoAutorizacaoTemp(scoDireitoAutorizacaoTemp);

	this.getScoDireitoAutorizacaoTempDAO().removerPorId(scoDireitoAutorizacaoTemp.getNumero());
    }

    // instâncias
    protected ScoDireitoAutorizacaoTempDAO getScoDireitoAutorizacaoTempDAO() {
	return scoDireitoAutorizacaoTempDAO;
    }

    protected ScoPontoServidorDAO getScoPontoServidorDAO() {
	return scoPontoServidorDAO;
    }

}
