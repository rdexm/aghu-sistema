package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MptAgendaPrescricao;
import br.gov.mec.aghu.model.MptProtocoloCuidados;
import br.gov.mec.aghu.model.MptProtocoloMedicamentos;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.prescricaomedica.dao.MptProtocoloCuidadosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloMedicamentosDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.DiaPrescricaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloMedicamentoSolucaoCuidadoVO;

@Stateless
public class ProcedimentoTerapeuticoON extends BaseBusiness {


	@EJB
	private ProcedimentoTerapeuticoRN procedimentoTerapeuticoRN;
	
	@Inject
	private MptProtocoloMedicamentosDAO mptProtocoloMedicamentosDAO;
	
	@Inject
	private MptProtocoloCuidadosDAO mptProtocoloCuidadosDAO;

	@Inject
	private MptCaracteristicaTipoSessaoDAO mptCaracteristicaTipoSessaoDAO;

	private static final Log LOG = LogFactory.getLog(ProcedimentoTerapeuticoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
		
	private static enum AgendamentoSessaoExtraRNExceptionCode implements BusinessExceptionCode {
		SELECIONAR_DIA, HORA_INICIAL_OBRIGATORIA,OBRIGATORIEDADO_TIPO_SESSAO, OBRIGATORIEDADO_HORA_INICIAL_SESSAO;
	}	 

	/**
	 * 
	 */
	private static final long serialVersionUID = -1115351306638248746L;

	public void atualizaMptAgendaPrescricao(MptAgendaPrescricao agendaPrescricao, boolean flush) throws BaseException {
		this.getProcedimentoTerapeuticoRN().atualizaMptAgendaPrescricao(agendaPrescricao, flush);
	}

	protected ProcedimentoTerapeuticoRN getProcedimentoTerapeuticoRN() {
		return procedimentoTerapeuticoRN;
	}

	public void validarCampoObrigatorioAgendamentoSessaoExtra(MptTipoSessao tipoSessao, Date horaInicio, DiaPrescricaoVO diaPrescricaoVOSelecionado) throws ApplicationBusinessException{
		if(tipoSessao == null || tipoSessao.getSeq() == null){
			throw new ApplicationBusinessException(AgendamentoSessaoExtraRNExceptionCode.OBRIGATORIEDADO_TIPO_SESSAO);
		}
		if(horaInicio == null){
			throw new ApplicationBusinessException(AgendamentoSessaoExtraRNExceptionCode.OBRIGATORIEDADO_HORA_INICIAL_SESSAO);
		}
		if(diaPrescricaoVOSelecionado == null || diaPrescricaoVOSelecionado.getSeq() == null){
			throw new ApplicationBusinessException(AgendamentoSessaoExtraRNExceptionCode.SELECIONAR_DIA);
		}
	}
	
	public Date relacionarDiaSelecionado(Date horaInicio, DiaPrescricaoVO diaPrescricaoVO) throws ApplicationBusinessException{
		
		if(horaInicio != null){
			
			Integer horas;
			Integer min;
			
			if(diaPrescricaoVO.getTempoAdministracao() != null){
				horas = diaPrescricaoVO.getTempoAdministracao() / 60;
				min = diaPrescricaoVO.getTempoAdministracao() % 60;
			}else{
				horas = 0;
				min = 0;
			}
			 
			Date horaFim = DateUtil.adicionaHoras(horaInicio, horas);
			return horaFim = DateUtil.adicionaMinutos(horaFim, min);
		}else{
			throw new ApplicationBusinessException(AgendamentoSessaoExtraRNExceptionCode.HORA_INICIAL_OBRIGATORIA);
		}
	}
	
	public void moverOrdemCima(ProtocoloMedicamentoSolucaoCuidadoVO selecionado, ProtocoloMedicamentoSolucaoCuidadoVO anterior) {
		if (selecionado.getPtmSeq() != null) {
			MptProtocoloMedicamentos itemSelecionado =  mptProtocoloMedicamentosDAO.obterPorChavePrimaria(selecionado.getPtmSeq());
			itemSelecionado.setOrdem(((Integer)(itemSelecionado.getOrdem() - 1)).shortValue());
			mptProtocoloMedicamentosDAO.atualizar(itemSelecionado);
		} else if (selecionado.getPcuSeq() != null) {
			MptProtocoloCuidados itemSelecionado =  mptProtocoloCuidadosDAO.obterPorChavePrimaria(selecionado.getPcuSeq());
			itemSelecionado.setOrdem(((Integer)(itemSelecionado.getOrdem() - 1)).shortValue());
			mptProtocoloCuidadosDAO.atualizar(itemSelecionado);
		}
		if (anterior.getPtmSeq() != null) {
			MptProtocoloMedicamentos itemAnterior =  mptProtocoloMedicamentosDAO.obterPorChavePrimaria(anterior.getPtmSeq());
			itemAnterior.setOrdem(((Integer)(itemAnterior.getOrdem() + 1)).shortValue());
			mptProtocoloMedicamentosDAO.atualizar(itemAnterior);
		} else if (anterior.getPcuSeq() != null) {
			MptProtocoloCuidados itemAnterior =  mptProtocoloCuidadosDAO.obterPorChavePrimaria(anterior.getPcuSeq());
			itemAnterior.setOrdem(((Integer)(itemAnterior.getOrdem() + 1)).shortValue());
			mptProtocoloCuidadosDAO.atualizar(itemAnterior);
		}
	}
	
	public void moverOrdemBaixo(ProtocoloMedicamentoSolucaoCuidadoVO selecionado, ProtocoloMedicamentoSolucaoCuidadoVO posterior) {
		if (selecionado.getPtmSeq() != null) {
			MptProtocoloMedicamentos itemSelecionado =  mptProtocoloMedicamentosDAO.obterPorChavePrimaria(selecionado.getPtmSeq());
			itemSelecionado.setOrdem(((Integer)(itemSelecionado.getOrdem() + 1)).shortValue());
			mptProtocoloMedicamentosDAO.atualizar(itemSelecionado);
		} else if (selecionado.getPcuSeq() != null) {
			MptProtocoloCuidados itemSelecionado =  mptProtocoloCuidadosDAO.obterPorChavePrimaria(selecionado.getPcuSeq());
			itemSelecionado.setOrdem(((Integer)(itemSelecionado.getOrdem() + 1)).shortValue());
			mptProtocoloCuidadosDAO.atualizar(itemSelecionado);
		}
		
		if (posterior.getPtmSeq() != null) {
			MptProtocoloMedicamentos itemPosterior =  mptProtocoloMedicamentosDAO.obterPorChavePrimaria(posterior.getPtmSeq());
			itemPosterior.setOrdem(((Integer)(itemPosterior.getOrdem() - 1)).shortValue());
			mptProtocoloMedicamentosDAO.atualizar(itemPosterior);
		} else if (posterior.getPcuSeq() != null) {
			MptProtocoloCuidados itemPosterior =  mptProtocoloCuidadosDAO.obterPorChavePrimaria(posterior.getPcuSeq());
			itemPosterior.setOrdem(((Integer)(itemPosterior.getOrdem() - 1)).shortValue());
			mptProtocoloCuidadosDAO.atualizar(itemPosterior);
		}
	}
	
	public boolean exibirColunaApac(Short tipoSessaoSeq){
		return mptCaracteristicaTipoSessaoDAO.existeCaracteristicaTipoSessao("APAC", tipoSessaoSeq);
	}
	
	public boolean exibirColuna(List<String> listaSiglas , String sigla){
		if(listaSiglas != null && !listaSiglas.isEmpty()){
			if(sigla !=  null && !sigla.isEmpty()){
				for (String itemSigla : listaSiglas) {
					if(itemSigla.equals(sigla)){
						return true;
					}
				}
			}
		}
		return false;
	}

}
