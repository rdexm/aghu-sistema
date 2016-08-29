package br.gov.mec.aghu.prescricaomedica.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioFormaCalculoAprazamento;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.dominio.DominioTipoUsoMdtoAntimicrobia;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdto;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdtoId;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmJustificativaUsoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAprazamentoFrequenciasDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescParecerMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmJustificativaUsoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicamentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificaAtendimentoVO;

/**
 * Implementação das packages MPMK_IME e MPMK_IME_RN.
 */

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class ItemPrescricaoMedicamentoRN extends BaseBusiness implements
		Serializable {


@EJB
private PrescricaoMedicamentoRN prescricaoMedicamentoRN;

//@EJB
//private PrescricaoMedicaRN prescricaoMedicaRN;

//@EJB
//private ManterPrescricaoMedicamentoON manterPrescricaoMedicamentoON;

private static final Log LOG = LogFactory.getLog(ItemPrescricaoMedicamentoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmJustificativaUsoMdtoDAO mpmJustificativaUsoMdtoDAO;

@Inject
private MpmItemPrescParecerMdtoDAO mpmItemPrescParecerMdtoDAO;

@EJB
private IPacienteFacade pacienteFacade;

@Inject
private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;

@EJB
private IParametroFacade parametroFacade;

@Inject
private MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtoDAO;

@EJB
private IFarmaciaFacade farmaciaFacade;

@Inject
private MpmAprazamentoFrequenciasDAO mpmAprazamentoFrequenciasDAO;

@Inject
private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;

@EJB
private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6987718723645201178L;

	private enum ItemPrescricaoMedicamentoExceptionCode implements
			BusinessExceptionCode {
		MPM_00072, MPM_00794, MPM_00826, MPM_00846, MPM_00851, MPM_00852, MPM_00861, MPM_01128, MPM_01130, MPM_01131, MPM_01132, MPM_01141, MPM_01142, MPM_01148, MPM_01149, MPM_01165, MPM_01167, MPM_01206, MPM_01277, MPM_01393, MPM_01394, MPM_01397, MPM_01398, MPM_01422, 
		MPM_01578, MPM_01596, MPM_01597, MPM_02011, MPM_02198, MPM_00841, MPM_01195, MPM_01128_MEDICAMENTO_SOLUCAO, MEDICAMENTO_NAO_ENCONTRADO,
		MPM_02197, MPM_01559, MPM_01553, MPM_02233, MPM_02232, MENSAGEM_NENHUMA_FREQUENCIA_APRAZAMENTO_ECONTRADA, CAMPO_DOSE_OBRIGATORIO;
	}
	
	public MpmItemPrescricaoMdto reatachar(MpmItemPrescricaoMdto prescricaoMedicamento) {
		if(getMpmItemPrescricaoMdtoDAO().contains(prescricaoMedicamento)) {
			return getMpmItemPrescricaoMdtoDAO().merge(prescricaoMedicamento);
		}
		return prescricaoMedicamento;
	}

	public void inserirItemPrescricaoMedicamento(MpmItemPrescricaoMdto itemPrescricaoMedicamento) throws ApplicationBusinessException {
		// Chama o método inserir considerando que o item não foi originado de uma cópia 
		// quando da criação de uma nova Prescrição Médica
		inserirItemPrescricaoMedicamento(itemPrescricaoMedicamento, false);
	}
	
	public void inserirItemPrescricaoMedicamento(
			MpmItemPrescricaoMdto itemPrescricaoMedicamento, Boolean isCopiado) throws ApplicationBusinessException {
		// Chamada de trigger "before each row"
		this.preInserir(itemPrescricaoMedicamento, isCopiado);
		
		getMpmItemPrescricaoMdtoDAO().persistir(itemPrescricaoMedicamento);
		
		// Chamada de trigger "after statement" (enforce)
		this.enforceItemPrescricaoMedicamentoInclusao(itemPrescricaoMedicamento, isCopiado);
	}
	
	public void inserirItemPrescricaoMedicamentoModeloBasico(
			MpmItemPrescricaoMdto itemPrescricaoMedicamento) throws ApplicationBusinessException {
		// Chamada de trigger "before each row"
		this.preInserirModeloBasico(itemPrescricaoMedicamento);
		
		getMpmItemPrescricaoMdtoDAO().persistir(itemPrescricaoMedicamento);
		getMpmItemPrescricaoMdtoDAO().flush();
		
		// Chamada de trigger "after statement" (enforce)
		this.enforceItemPrescricaoMedicamentoInclusao(itemPrescricaoMedicamento, false);
	}

	public void atualizarItemPrescricaoMedicamento(
			MpmItemPrescricaoMdto itemPrescricaoMedicamento) throws ApplicationBusinessException {
		// Chamada de trigger "before each row"
		this.preAtualizar(itemPrescricaoMedicamento);
		
		getMpmItemPrescricaoMdtoDAO().merge(itemPrescricaoMedicamento);
		
		// Chamada de trigger "after statement" (enforce)O
		this.enforceItemPrescricaoMedicamentoEdicao(itemPrescricaoMedicamento);
	}

	public void excluirItemPrescricaoMedicamento(
			MpmItemPrescricaoMdto itemPrescricaoMedicamento, Boolean autoExcluirProcedimentoMedicamentoSol) throws ApplicationBusinessException {
		this.getMpmItemPrescricaoMdtoDAO().remover(itemPrescricaoMedicamento);
		
		// Chamada de trigger "after statement" (enforce)
		enforceItemPrescricaoMedicamentoDelecao(itemPrescricaoMedicamento, autoExcluirProcedimentoMedicamentoSol);
	}

	/**
	 * ORADB Trigger MPMT_IME_BRU (operacao UPD)
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void preAtualizar(MpmItemPrescricaoMdto itemPrescricaoMdto)
			throws ApplicationBusinessException {
		
		ItemPrescricaoMedicamentoVO itemPrescMdtoVO = getMpmItemPrescricaoMdtoDAO().obterItemPrescricaoMdtoVO(itemPrescricaoMdto.getId().getPmdAtdSeq(), itemPrescricaoMdto.getId().getPmdSeq(), itemPrescricaoMdto.getId().getMedMatCodigo(), itemPrescricaoMdto.getId().getSeqp());
		
		//verificar se o medicamento esta ativo
		if(!CoreUtil.igual(itemPrescMdtoVO.getMatCodigo(), itemPrescricaoMdto.getId().getMedMatCodigo())) {
			verificaMedicamento(itemPrescricaoMdto.getId().getMedMatCodigo());
		}
		
		//FORMA_DOSAGEM deve ser ativa e compatível
		//com o medicamento prescrito
		if(!CoreUtil.igual(itemPrescMdtoVO.getMatCodigo(), itemPrescricaoMdto.getId().getMedMatCodigo()) || !CoreUtil.igual(itemPrescMdtoVO.getFdsSeq(), (itemPrescricaoMdto.getFormaDosagem() != null)?itemPrescricaoMdto.getFormaDosagem().getSeq():null)) {
			verificaFormaDosagem(itemPrescricaoMdto.getId().getMedMatCodigo(), itemPrescricaoMdto.getFormaDosagem().getSeq());
		}
		
		// Regra da justificativa desativada conforme defeitos 70697, 70699 e 70701
		//Exigir informacao de Justificativa Uso Mdto se o Medicamento
		//relacionado possui ind_exige_justificativa = 'S' no Tipo Uso Mdto
//		DominioIndPendenteItemPrescricao indPendente = verificaJustificativa(itemPrescricaoMdto.getId().getMedMatCodigo(), (itemPrescricaoMdto.getJustificativaUsoMedicamento() != null)?itemPrescricaoMdto.getJustificativaUsoMedicamento().getSeq():null, itemPrescricaoMdto.getId().getPmdAtdSeq(), itemPrescricaoMdto.getId().getPmdSeq());
		//
		//Verificar se o convênio exige justificativa para o medicamento
//		if(DominioIndPendenteItemPrescricao.N.equals(indPendente)) {
//			
//			Short codigoConvenioSaude = null;
//
//			Byte seqConvenioSaudePlano= null;
//
//			VerificaAtendimentoVO verificaAtendimentoVO = verificaAtendimento(null, null, itemPrescricaoMdto.getId().getPmdAtdSeq(),null,null,null);
//			if(verificaAtendimentoVO.getSeqInternacao() != null) {
//				BuscaDadosInternacaoVO buscaDadosInternacaoVO = getPrescricaoMedicaRN().buscaDadosInternacao(verificaAtendimentoVO.getSeqInternacao());
//				codigoConvenioSaude = buscaDadosInternacaoVO.getCodigoConvenioSaude();
//				seqConvenioSaudePlano = buscaDadosInternacaoVO.getSeqConvenioSaudePlano();
//			}
//			else if(verificaAtendimentoVO.getSeqAtendimentoUrgencia() != null) {
//				BuscaDadosAtendimentoUrgenciaVO buscaDadosAtendimentoUrgenciaVO = getPrescricaoMedicaRN().buscaDadosAtendimentoUrgencia(verificaAtendimentoVO.getSeqAtendimentoUrgencia());
//				codigoConvenioSaude = buscaDadosAtendimentoUrgenciaVO.getCodigoConvenioSaude();
//				seqConvenioSaudePlano = buscaDadosAtendimentoUrgenciaVO.getCspSeq();
//			}
//			else if(verificaAtendimentoVO.getSeqHospitalDia() != null) {
//				BuscaDadosHospitalDiaVO buscaDadosHospitalDiaVO = getPrescricaoMedicaRN().buscaDadosHospitalDia(verificaAtendimentoVO.getSeqHospitalDia());
//				codigoConvenioSaude = buscaDadosHospitalDiaVO.getCodigoConvenioSaude();
//				seqConvenioSaudePlano = buscaDadosHospitalDiaVO.getCspSeq();
//			}
//			
//			Integer phiSeq = getPrescricaoMedicaRN().verificaProcedimentoHospitalarInterno(itemPrescricaoMdto.getId().getMedMatCodigo(), null, null);
//			
//			VerificaIndicadoresConvenioInternacaoVO verificaIndicadoresConvenioInternacaoVO = getPrescricaoMedicaRN().verificaIndicadoresConvenioInternacao(codigoConvenioSaude,seqConvenioSaudePlano,phiSeq);
//			if(itemPrescricaoMdto.getJustificativaUsoMedicamento() == null  && (verificaIndicadoresConvenioInternacaoVO != null && verificaIndicadoresConvenioInternacaoVO.getIndExigeJustificativa())) {
//				verificaConvenio();	
//			}
//		}
		
		//Esta regra destina a verificar se o item prescrição mdto possuir um
		//medicamento onde tipo uso mdto possuir ind_antimicrobiano = 'S',
		//entao ind_tipo_uso_mdto_antimicrobiano deve estar informado.
		
		if(!CoreUtil.igual(itemPrescMdtoVO.getUsoMdtoAntimicrobia(), itemPrescricaoMdto.getUsoMdtoAntimicrobia()) 
				|| !CoreUtil.igual(itemPrescMdtoVO.getMatCodigo(), itemPrescricaoMdto.getId().getMedMatCodigo()) 
				|| itemPrescricaoMdto.getJustificativaUsoMedicamento() != null) 
		{
			verificaItemPrescricaoPossuiMedicamentoAntimicrobiano(itemPrescricaoMdto.getId().getMedMatCodigo(), itemPrescricaoMdto.getUsoMdtoAntimicrobia(), itemPrescricaoMdto.getId().getPmdAtdSeq(), itemPrescricaoMdto.getId().getPmdSeq(), "U", itemPrescricaoMdto.getJustificativaUsoMedicamento().getSeq());	
		}
		//Se informado duracao trat aprovado, a Prescricao Mdto deve
		//possuir duracao trat solicitado >0
		if(!CoreUtil.igual(itemPrescMdtoVO.getDuracaoTratAprovado(), itemPrescricaoMdto.getDuracaoTratAprovado())
				|| !CoreUtil.igual(itemPrescMdtoVO.getPmdAtdSeq(), itemPrescricaoMdto.getId().getPmdAtdSeq()) 
				|| !CoreUtil.igual(itemPrescMdtoVO.getPmdSeq(), itemPrescricaoMdto.getId().getPmdSeq())) {
			verificaDuracaoTratamentoSolicitado(itemPrescricaoMdto.getId().getPmdAtdSeq(), itemPrescricaoMdto.getId().getPmdSeq());			
		}
		
		//Para informar qtde mg kg, exigir a existencia da
		//informacao peso do paciente cuja data criado_em
		//seja a mais atual (max) e seja >= a data do Aten
		//dimento a que a Prescricao Mdto esta relacionada
		if(!CoreUtil.igual(itemPrescMdtoVO.getQtdeMgKg(), itemPrescricaoMdto.getQtdeMgKg())
				|| !CoreUtil.igual(itemPrescMdtoVO.getPmdAtdSeq(), itemPrescricaoMdto.getId().getPmdAtdSeq())) {
			verificaPesoKg(itemPrescricaoMdto.getId().getPmdAtdSeq());			
		}
		//Para informar qtde mg superficie corporal, exigir
		//a existencia da informacao peso do paciente cuja
		//data criado_em seja a mais atual (max) e seja
		//>= a data do Atendimento a que a Prescricao Mdto
		//esta relacionada.
		if(!CoreUtil.igual(itemPrescMdtoVO.getQtdeMgSuperfCorporal(), itemPrescricaoMdto.getQtdeMgSuperfCorporal())
				|| !CoreUtil.igual(itemPrescMdtoVO.getPmdAtdSeq(), itemPrescricaoMdto.getId().getPmdAtdSeq())) {
			verificaPesoSuperficieCorporal(itemPrescricaoMdto.getId().getPmdAtdSeq());			
		}
		//Esta regra destina a verificar se o ind_exige_observacao = 'S'
		//para o Medicamento relacionado então o campo observacao na tabela
		//item prescricao mdto deve ser not null.
		if(!CoreUtil.igual(itemPrescMdtoVO.getMatCodigo(), itemPrescricaoMdto.getId().getMedMatCodigo())
				|| !CoreUtil.igual(itemPrescMdtoVO.getObservacao(), itemPrescricaoMdto.getObservacao())) {
			verificaExigeObservacao(itemPrescricaoMdto.getId().getMedMatCodigo(), itemPrescricaoMdto.getObservacao());			
		}
		
		//dose deve ser um numero inteiro (não fracionada)
		//se ind_permite_dose_fracionada = 'N' no Medica -
		//mento relacionado
		if(!CoreUtil.igual(itemPrescMdtoVO.getMatCodigo(), itemPrescricaoMdto.getId().getMedMatCodigo())
				|| !CoreUtil.igual(itemPrescMdtoVO.getDose(), itemPrescricaoMdto.getDose())) {
			verificaDoseFracionada(itemPrescricaoMdto.getId().getMedMatCodigo(), itemPrescricaoMdto.getDose(), itemPrescricaoMdto.getFormaDosagem().getSeq());			
		}

		//Somente pode ser informada duracao trat aprovado
		//se houver associativa Item Prescr Parecer Mdto
		if(itemPrescricaoMdto.getDuracaoTratAprovado() != null) {
			if(!CoreUtil.igual(itemPrescMdtoVO.getDuracaoTratAprovado(), itemPrescricaoMdto.getDuracaoTratAprovado())
					|| !CoreUtil.igual(itemPrescMdtoVO.getPmdAtdSeq(), itemPrescricaoMdto.getId().getPmdAtdSeq()) 
					|| !CoreUtil.igual(itemPrescMdtoVO.getPmdSeq(), itemPrescricaoMdto.getId().getPmdSeq())
					|| !CoreUtil.igual(itemPrescMdtoVO.getMatCodigo(), itemPrescricaoMdto.getId().getMedMatCodigo())
					|| !CoreUtil.igual(itemPrescMdtoVO.getSeqp(), itemPrescricaoMdto.getId().getSeqp())
					) {
				verificaTratamentoAprovado(itemPrescricaoMdto.getId().getPmdAtdSeq(), Integer.valueOf(itemPrescricaoMdto.getId().getPmdSeq().intValue()), itemPrescricaoMdto.getId().getMedMatCodigo(), itemPrescricaoMdto.getId().getSeqp());
			}			
		}
		
		verificaAlteracaoOrigemJustificativa(itemPrescMdtoVO.getOrigemJustificativa(), itemPrescricaoMdto.getOrigemJustificativa());
	}
	
	/**
	 * ORADB Trigger MPMT_IME_BRI (operacao INS)
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void preInserir(MpmItemPrescricaoMdto itemPrescricaoMdto, Boolean isCopiado)
			throws ApplicationBusinessException {
		//verificar se o mestre (mpm_prescricao_mdtos esta com ind_pendente igual a 'P' ou 'B'.
		verificaInclusao(itemPrescricaoMdto.getId().getPmdAtdSeq(), itemPrescricaoMdto.getId().getPmdSeq());
		
		if (!isCopiado){
			if (itemPrescricaoMdto.getJustificativaUsoMedicamento() != null) {
				//Esta regra destina a verificar se o item prescrição mdto possuir um medicamento onde tipo uso mdto 
				//possuir ind_antimicrobiano = 'S', entao ind_tipo_uso_mdto_antimicrobiano deve estar informado.
				verificaItemPrescricaoPossuiMedicamentoAntimicrobiano(itemPrescricaoMdto.getId().getMedMatCodigo(),
						itemPrescricaoMdto.getUsoMdtoAntimicrobia(),itemPrescricaoMdto.getId().getPmdAtdSeq(),
						itemPrescricaoMdto.getId().getPmdSeq(),"I",itemPrescricaoMdto.getJustificativaUsoMedicamento().getSeq());
			}			
		}
		
		//Para informar qtde mg kg, exigir a existencia da informacao peso do paciente cuja data criado_em 
		//seja a mais atual (max) e seja >= a data do Atendimento a que a Prescricao Mdto esta relacionada.
		if (itemPrescricaoMdto.getQtdeMgKg() != null){
			verificaPesoKg(itemPrescricaoMdto.getId().getPmdAtdSeq());
		}
		
		//Para informar qtde mg superficie corporal, exigir	a existencia da informacao peso do paciente cuja data
		//criado_em seja a mais atual (max) e seja	>= a data do Atendimento a que a Prescricao Mdto esta relacionada.
		if (itemPrescricaoMdto.getQtdeMgSuperfCorporal() != null){
			verificaPesoSuperficieCorporal(itemPrescricaoMdto.getId().getPmdAtdSeq());			
		}
		
		//Esta regra destina a verificar se o ind_exige_observacao = 'S' para o Medicamento relacionado
		//então o campo observacao na tabela item prescricao mdto deve ser not null.
		if (!isCopiado) {
			verificaExigeObservacao(itemPrescricaoMdto.getId().getMedMatCodigo(), itemPrescricaoMdto.getObservacao());
		}
		
		//Dose deve ser um numero inteiro (não fracionada) se ind_permite_dose_fracionada = 'N' no Medica -	mento relacionado.
		if (!isCopiado){
			verificaDoseFracionada(itemPrescricaoMdto.getId().getMedMatCodigo(), 
					itemPrescricaoMdto.getDose(), (itemPrescricaoMdto.getFormaDosagem() != null) ? itemPrescricaoMdto.getFormaDosagem().getSeq() : null);			
		}
		
		
		//Se informado duracao trat aprovado, a Prescricao Mdto deve possuir duracao trat solicitado >0.
		if(itemPrescricaoMdto.getDuracaoTratAprovado() != null){
			verificaDuracaoTratamentoSolicitado(itemPrescricaoMdto.getId().getPmdAtdSeq(), 
					itemPrescricaoMdto.getId().getPmdSeq());
		}

		//Atribui valor default 'N' para ind_origem_justif.
		atualizaDefaultOrigemJustificativa(itemPrescricaoMdto);
		
	}
	
	/**
	 * ORADB Trigger MPMT_IME_BRI (operacao INS)
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void preInserirModeloBasico(MpmItemPrescricaoMdto itemPrescricaoMdto)
			throws ApplicationBusinessException {
		//verificar se o mestre (mpm_prescricao_mdtos esta com ind_pendente igual a 'P' ou 'B'.
		verificaInclusao(itemPrescricaoMdto);
		
		if (itemPrescricaoMdto.getJustificativaUsoMedicamento() != null) {
			//Esta regra destina a verificar se o item prescrição mdto possuir um medicamento onde tipo uso mdto 
			//possuir ind_antimicrobiano = 'S', entao ind_tipo_uso_mdto_antimicrobiano deve estar informado.
			verificaItemPrescricaoPossuiMedicamentoAntimicrobiano(itemPrescricaoMdto.getId().getMedMatCodigo(),
					itemPrescricaoMdto.getUsoMdtoAntimicrobia(),itemPrescricaoMdto.getId().getPmdAtdSeq(),
					itemPrescricaoMdto.getId().getPmdSeq(),"I",itemPrescricaoMdto.getJustificativaUsoMedicamento().getSeq());
		}
		
		//Para informar qtde mg kg, exigir a existencia da informacao peso do paciente cuja data criado_em 
		//seja a mais atual (max) e seja >= a data do Atendimento a que a Prescricao Mdto esta relacionada.
		if (itemPrescricaoMdto.getQtdeMgKg() != null){
			verificaPesoKg(itemPrescricaoMdto.getId().getPmdAtdSeq());
		}
		
		//Para informar qtde mg superficie corporal, exigir	a existencia da informacao peso do paciente cuja data
		//criado_em seja a mais atual (max) e seja	>= a data do Atendimento a que a Prescricao Mdto esta relacionada.
		if (itemPrescricaoMdto.getQtdeMgSuperfCorporal() != null){
			verificaPesoSuperficieCorporal(itemPrescricaoMdto.getId().getPmdAtdSeq());			
		}
		
		//Esta regra destina a verificar se o ind_exige_observacao = 'S' para o Medicamento relacionado
		//então o campo observacao na tabela item prescricao mdto deve ser not null.
		verificaExigeObservacao(itemPrescricaoMdto.getId().getMedMatCodigo(), 
				itemPrescricaoMdto.getObservacao());
		
		//Dose deve ser um numero inteiro (não fracionada) se ind_permite_dose_fracionada = 'N' no Medica -	mento relacionado.
		verificaDoseFracionada(itemPrescricaoMdto.getId().getMedMatCodigo(), 
				itemPrescricaoMdto.getDose(), (itemPrescricaoMdto.getFormaDosagem() != null) ? itemPrescricaoMdto.getFormaDosagem().getSeq() : null);
		
		
		//Se informado duracao trat aprovado, a Prescricao Mdto deve possuir duracao trat solicitado >0.
		if(itemPrescricaoMdto.getDuracaoTratAprovado() != null){
			verificaDuracaoTratamentoSolicitado(itemPrescricaoMdto.getId().getPmdAtdSeq(), 
					itemPrescricaoMdto.getId().getPmdSeq());
		}

		//Atribui valor default 'N' para ind_origem_justif.
		atualizaDefaultOrigemJustificativa(itemPrescricaoMdto);
		
	}
	
	/**
	 * Método que implementa a enforce MPMP_ENFORCE_IME_RULES para o evento de
	 * 'INSERT
	 * 
	 * ORADB MPMP_ENFORCE_IME_RULES
	 * 
	 * @param MpmItemPrescricaoMdto
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void enforceItemPrescricaoMedicamentoInclusao(MpmItemPrescricaoMdto itemPrescricaoMdto, Boolean isCopiado)
			throws ApplicationBusinessException {
		atualizaSolucao(itemPrescricaoMdto.getId().getPmdAtdSeq(), 
			itemPrescricaoMdto.getId().getPmdSeq(), Boolean.FALSE);
		
		atualizaInicioTratamento(itemPrescricaoMdto.getId().getPmdAtdSeq(), 
				itemPrescricaoMdto.getId().getPmdSeq(), itemPrescricaoMdto.getId().getMedMatCodigo());
		
		if (!isCopiado){
			/*   Esta regra destina a verificar se o item prescrição mdto possuir um
	      medicamento onde tipo uso mdto possuir ind_exige_duracao_solicitada =
	      'S',   entao duracao trat solicitado na prescrição mdto não pode ser null.*/
			if (itemPrescricaoMdto.getJustificativaUsoMedicamento() != null){
				verificaExigeDuracaoSolicitada(itemPrescricaoMdto.getId().getMedMatCodigo(),
						itemPrescricaoMdto.getId().getPmdAtdSeq(),
						itemPrescricaoMdto.getId().getPmdSeq(), "I");
			}			
		}
	}

	
	/**
	 * Método que implementa a enforce MPMP_ENFORCE_IME_RULES para o evento de
	 * 'UPDATE
	 * 
	 * ORADB MPMP_ENFORCE_IME_RULES
	 * 
	 * @param MpmItemPrescricaoMdto
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void enforceItemPrescricaoMedicamentoEdicao(MpmItemPrescricaoMdto itemPrescricaoMdto)
			throws ApplicationBusinessException {
		
		ItemPrescricaoMedicamentoVO itemPrescricaoMedicamentoVO = new ItemPrescricaoMedicamentoVO();
		itemPrescricaoMedicamentoVO = getMpmItemPrescricaoMdtoDAO().obterItemPrescricaoMdtoVO(itemPrescricaoMdto.getId().getPmdAtdSeq(), itemPrescricaoMdto.getId().getPmdSeq(), itemPrescricaoMdto.getId().getMedMatCodigo(), itemPrescricaoMdto.getId().getSeqp());

	     /*   Esta regra destina a verificar se o item prescrição mdto possuir um
	      medicamento onde tipo uso mdto possuir ind_exige_duracao_solicitada =
	      'S',   entao duracao trat solicitado na prescrição mdto não pode ser null.*/
		if(CoreUtil.modificados(itemPrescricaoMedicamentoVO.getMatCodigo(), itemPrescricaoMdto.getId().getMedMatCodigo()) 
				|| CoreUtil.modificados(itemPrescricaoMedicamentoVO.getPmdAtdSeq(), itemPrescricaoMdto.getId().getPmdAtdSeq())
				|| CoreUtil.modificados(itemPrescricaoMedicamentoVO.getPmdSeq(), itemPrescricaoMdto.getId().getPmdSeq())
				|| (itemPrescricaoMdto.getJustificativaUsoMedicamento() != null && itemPrescricaoMdto.getJustificativaUsoMedicamento().getSeq() != null))
		{
			verificaExigeDuracaoSolicitada(itemPrescricaoMdto.getId().getMedMatCodigo(),
					itemPrescricaoMdto.getId().getPmdAtdSeq(),
					itemPrescricaoMdto.getId().getPmdSeq(), "I");
		}
	}

	/**
	 * Método que implementa a enforce MPMP_ENFORCE_IME_RULES para o evento de
	 * 'UPDATE
	 * 
	 * ORADB MPMP_ENFORCE_IME_RULES
	 * 
	 * @param MpmItemPrescricaoMdto
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void enforceItemPrescricaoMedicamentoDelecao(MpmItemPrescricaoMdto itemPrescricaoMdto, Boolean autoExcluirProcedimentoMedicamentoSol)
			throws ApplicationBusinessException {
		
	     /*  chama a procedure que lê a tabela de itens de mdtos para verificar
        se é o último item */
		verificaUltimoItem(itemPrescricaoMdto.getId().getPmdAtdSeq(), itemPrescricaoMdto.getId().getPmdSeq(), autoExcluirProcedimentoMedicamentoSol);
		
		atualizaSolucao(itemPrescricaoMdto.getId().getPmdAtdSeq(),  itemPrescricaoMdto.getId().getPmdSeq(), autoExcluirProcedimentoMedicamentoSol);

	     /* nao posso deletar um item de nedicamento se o medicamento  ja tiver sido
	    validado */
		verificaDelecao(itemPrescricaoMdto.getId().getPmdAtdSeq(), itemPrescricaoMdto.getId().getPmdSeq(), autoExcluirProcedimentoMedicamentoSol);
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_ANTIMIC
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaItemPrescricaoPossuiMedicamentoAntimicrobiano(Integer codigoMedicamento,
			DominioTipoUsoMdtoAntimicrobia indUsoMedicamentoAntimicrobial, Integer pmdAtdSeq,
			Long pmdSeq, String operacao, Integer jumSeq)
			throws ApplicationBusinessException {
		// Esta regra destina a verificar se o item prescrição mdto possuir um
		// medicamento onde tipo uso mdto possuir ind_antimicrobiano = 'S',
		// entao ind_tipo_uso_mdto_antimicrobiano deve estar informado.
		if ("I".equals(operacao)) {
			MpmPrescricaoMdto mpmPrescricaoMdto = getMpmPrescricaoMdtoDAO()
					.obterPorChavePrimaria(
							new MpmPrescricaoMdtoId(pmdAtdSeq, pmdSeq));

			if (mpmPrescricaoMdto != null
					&& DominioIndPendenteItemPrescricao.B.equals(mpmPrescricaoMdto
							.getIndPendente())) {
				return;
			}
		}

		if (jumSeq != null) {
			MpmJustificativaUsoMdto justificativaUsoMedicamentos = getMpmJustificativaUsoMdtosDAO()
					.obterJustificativaUsoMdtos(jumSeq);

			if (justificativaUsoMedicamentos != null
					&& justificativaUsoMedicamentos.getServidorValida() != null) {
				return;
			}
		}

		if (indUsoMedicamentoAntimicrobial == null) {
			AfaMedicamento medicamento = getFarmaciaFacade()
					.obterMedicamento(codigoMedicamento);

			if (medicamento != null) {
				StringBuilder descricao = new StringBuilder();

				descricao.append(medicamento.getDescricao());
				if (medicamento.getConcentracao() != null) {
					descricao.append(' ');
					descricao.append(medicamento.getConcentracao());
				}
				if (medicamento.getMpmUnidadeMedidaMedicas() != null) {
					descricao.append(' ');
					descricao.append(medicamento.getMpmUnidadeMedidaMedicas()
							.getDescricao());
				}

				throw new ApplicationBusinessException(
						ItemPrescricaoMedicamentoExceptionCode.MPM_00841,
						descricao.toString());
			}
		}
	}
	
	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_ATU_INI_TRAT
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void atualizaInicioTratamento(Integer seqAtendimento,
			Long seqPrescricaoMedicamento, Integer codigoMedicamento)
			throws ApplicationBusinessException {
		// Informar data inicio trat. quando medicamento antimicrobiano
		AfaMedicamento medicamento = getFarmaciaFacade().obterMedicamento(
				codigoMedicamento);

		if (medicamento != null && medicamento.getAfaTipoUsoMdtos() != null
				&& medicamento.getAfaTipoUsoMdtos().getIndAntimicrobiano()) {
			MpmPrescricaoMdto mpmPrescricaoMdto = getMpmPrescricaoMdtoDAO()
					.obterPorChavePrimaria(
							new MpmPrescricaoMdtoId(seqAtendimento,
									seqPrescricaoMedicamento));

			Calendar dataHoraInicioTratamentoCal = null;
			if (mpmPrescricaoMdto != null) {
				dataHoraInicioTratamentoCal = Calendar.getInstance();
				if (mpmPrescricaoMdto.getHoraInicioAdministracao() != null) {
					Calendar auxCal = Calendar.getInstance();
					auxCal.setTime(mpmPrescricaoMdto
							.getHoraInicioAdministracao());

					dataHoraInicioTratamentoCal.setTime(mpmPrescricaoMdto
							.getDthrInicio());
					dataHoraInicioTratamentoCal.set(Calendar.HOUR_OF_DAY,
							auxCal.get(Calendar.HOUR_OF_DAY));
					dataHoraInicioTratamentoCal.set(Calendar.MINUTE, auxCal
							.get(Calendar.MINUTE));
					dataHoraInicioTratamentoCal.set(Calendar.SECOND, auxCal
							.get(Calendar.SECOND));
					dataHoraInicioTratamentoCal.set(Calendar.MILLISECOND,
							auxCal.get(Calendar.MILLISECOND));

					auxCal.setTime(mpmPrescricaoMdto.getDthrInicio());
					if (dataHoraInicioTratamentoCal.before(auxCal)) {
						dataHoraInicioTratamentoCal.add(Calendar.DAY_OF_MONTH,
								1);
					}
				} else {
					dataHoraInicioTratamentoCal.setTime(mpmPrescricaoMdto
							.getDthrInicio());
				}
			}

			try {
				List<MpmPrescricaoMdto> prescricoesMedicamentos = getMpmPrescricaoMdtoDAO()
						.listarPrescricoesMedicamentosDataHoraInicioTratamentoNulo(
								seqAtendimento, seqPrescricaoMedicamento);

				if (prescricoesMedicamentos != null
						&& !prescricoesMedicamentos.isEmpty()) {
//					ManterPrescricaoMedicamentoON manterPrescricaoMedicamentoON = getManterPrescricaoMedicamentoON();

					for (MpmPrescricaoMdto prescricaoMedicamento : prescricoesMedicamentos) {
						prescricaoMedicamento
								.setDthrInicioTratamento(dataHoraInicioTratamentoCal != null ? dataHoraInicioTratamentoCal
										.getTime()
										: null);
						//manterPrescricaoMedicamentoON
						//.persistirPrescricaoMedicamento(prescricaoMedicamento);
						//@TODO : Rever a utilização dessa solução
//						getMpmPrescricaoMdtoDAO().flush();
					}
				}
			} catch (Exception e) {
				logError("Exceção capturada: ", e);
				throw new ApplicationBusinessException(
						ItemPrescricaoMedicamentoExceptionCode.MPM_02198);
			}
		}
	}
	
	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_DELECAO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaDelecao(Integer pmdAtdSeq, Long pmdSeq, Boolean autoExcluirProcedimentoMedicamentoSol)
			throws ApplicationBusinessException {
		// Não pode deletar um item de dieta se a dieta tiver sido validada.		
		if (autoExcluirProcedimentoMedicamentoSol == null
				|| !autoExcluirProcedimentoMedicamentoSol) {
			MpmPrescricaoMdto prescricaoMedicamento = getMpmPrescricaoMdtoDAO()
					.obterMedicamentoPeloId(pmdAtdSeq, pmdSeq);

			if (prescricaoMedicamento == null) {
				throw new ApplicationBusinessException(
						ItemPrescricaoMedicamentoExceptionCode.MPM_01398);
			} else if (DominioIndPendenteItemPrescricao.N.equals(prescricaoMedicamento
					.getIndPendente())) {
				throw new ApplicationBusinessException(
						ItemPrescricaoMedicamentoExceptionCode.MPM_01397);
			}
		}
	}
	
	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_INCLUSAO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaInclusao(Integer seqAtendimento, Long seqPrescricaoMedicamento)
			throws ApplicationBusinessException {
		// Não incluir um item de medicamento se o medicamento não tiver
		// ind_pendente igual a 'P', 'B', 'D' ou 'R'.
		MpmPrescricaoMdto mpmPrescricaoMdto = getMpmPrescricaoMdtoDAO()
				.obterPorChavePrimaria(
						new MpmPrescricaoMdtoId(seqAtendimento,
								seqPrescricaoMedicamento));
		verificaIndPendenteItemPrescricao(mpmPrescricaoMdto);
	}
	
	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_INCLUSAO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaInclusao(MpmItemPrescricaoMdto itemPrescricaoMdto)
			throws ApplicationBusinessException {
		// Não incluir um item de medicamento se o medicamento não tiver
		// ind_pendente igual a 'P', 'B', 'D' ou 'R'.
		MpmPrescricaoMdto mpmPrescricaoMdto = itemPrescricaoMdto.getPrescricaoMedicamento(); 
		verificaIndPendenteItemPrescricao(mpmPrescricaoMdto);
	}
	
	private void verificaIndPendenteItemPrescricao(MpmPrescricaoMdto mpmPrescricaoMdto) throws ApplicationBusinessException {
		if (mpmPrescricaoMdto != null) {
			DominioIndPendenteItemPrescricao indPendente = mpmPrescricaoMdto
					.getIndPendente();

			if (!DominioIndPendenteItemPrescricao.P.equals(indPendente)
					&& !DominioIndPendenteItemPrescricao.B.equals(indPendente)
					&& !DominioIndPendenteItemPrescricao.D.equals(indPendente)
					&& !DominioIndPendenteItemPrescricao.R.equals(indPendente)) {
				throw new ApplicationBusinessException(
						ItemPrescricaoMedicamentoExceptionCode.MPM_01393);
			}
		} else {
			throw new ApplicationBusinessException(
					ItemPrescricaoMedicamentoExceptionCode.MPM_01394);
		}
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_TRT_APR
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaTratamentoAprovado(Integer imePmdAtdSeq, Integer imePmdSeq,
			Integer imeMedMatCodigo, Short imeSeqp) throws ApplicationBusinessException {

		MpmItemPrescParecerMdtoId chave = new MpmItemPrescParecerMdtoId(imePmdAtdSeq, Long.valueOf(imePmdSeq), imeMedMatCodigo, imeSeqp);
		MpmItemPrescParecerMdto parecer = getMpmItemPrescParecerMdtoDAO().obterPorChavePrimaria(chave);
		if(parecer == null) {
			throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_01130);
		}
	}
	
	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_ALT_ORIG
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaAlteracaoOrigemJustificativa(			
			Boolean antigaIndOrigemJustificativa,
			Boolean novaIndOrigemJustificativa) throws ApplicationBusinessException {
		if(antigaIndOrigemJustificativa && !novaIndOrigemJustificativa) {
			throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_01578);
		}
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_DOSE_FRC
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaDoseFracionada(Integer codigoMedicamento, BigDecimal dose,
			Integer seqFormaDosagem) throws ApplicationBusinessException {
		// Dose deve ser um numero inteiro (não fracionada) se
		// ind_permite_dose_fracionada = 'N' no Medicamento relacionado.
		AfaMedicamento medicamento = getFarmaciaFacade().obterMedicamento(
				codigoMedicamento);
		
		if (medicamento != null) {
			if (!medicamento.getIndPermiteDoseFracionada()) {
				AfaFormaDosagem formaDosagem = getFarmaciaFacade()
						.obterAfaFormaDosagem(seqFormaDosagem);
				
				if (formaDosagem != null) {
					BigDecimal fatorConversaoUp = formaDosagem.getFatorConversaoUp();
					try {
						BigDecimal resultado = dose.divide(fatorConversaoUp);
						resultado.toBigIntegerExact();
					} catch (ArithmeticException e) {
						throw new ApplicationBusinessException(
								ItemPrescricaoMedicamentoExceptionCode.MPM_01128_MEDICAMENTO_SOLUCAO, medicamento.getDescricao());
					}					
				}
			}
		} else {
			throw new ApplicationBusinessException(
					ItemPrescricaoMedicamentoExceptionCode.MPM_01597);
		}
	}
	
	public void validarCampoDose(BigDecimal dose) throws ApplicationBusinessException{
		if(dose == null){
			throw new ApplicationBusinessException(
					ItemPrescricaoMedicamentoExceptionCode.CAMPO_DOSE_OBRIGATORIO);
		}
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_DR_TR_SL
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaDuracaoTratamentoSolicitado(
			Integer atdSeqPrescricaoMedicamento, Long seqPrescricaoMedicamento)
			throws ApplicationBusinessException {
		// Se informado duracao trat aprovado, a Prescricao Mdto deve possuir
		// duracao trat solicitado >0
		MpmPrescricaoMdto mpmPrescricaoMdto = getMpmPrescricaoMdtoDAO()
				.obterPorChavePrimaria(
						new MpmPrescricaoMdtoId(atdSeqPrescricaoMedicamento,
								seqPrescricaoMedicamento));
		
		if (mpmPrescricaoMdto == null
				|| (mpmPrescricaoMdto.getDuracaoTratSolicitado() == null || mpmPrescricaoMdto
						.getDuracaoTratSolicitado() <= 0)) {
			throw new ApplicationBusinessException(
					ItemPrescricaoMedicamentoExceptionCode.MPM_00826);
		}
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_EX_DURAC
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaExigeDuracaoSolicitada(Integer codigoMedicamento,
			Integer atdSeqPrescricaoMedicamento, Long seqPrescricaoMedicamento,
			String operacao) throws ApplicationBusinessException {

		if(getFarmaciaFacade().obtemTipoUsoMedicamentoComDuracSol(codigoMedicamento) != null) {
			MpmItemPrescricaoMdto item = getMpmItemPrescricaoMdtoDAO().obtemItemPrescricaoMedicamentoSemDuracaoTrat(codigoMedicamento, atdSeqPrescricaoMedicamento, seqPrescricaoMedicamento);
			if(item != null) {
				if(item.getPrescricaoMedicamento() != null) {
					if (!("I".equals(operacao) && item
							.getPrescricaoMedicamento().getIndPendente()
							.equals(DominioIndPendenteItemPrescricao.B))
							&& !(item.getJustificativaUsoMedicamento() != null && item
									.getJustificativaUsoMedicamento()
									.getServidorValida() != null)) {
						if (item.getPrescricaoMedicamento()
								.getDuracaoTratSolicitado() == null) {
							throw new ApplicationBusinessException(
									ItemPrescricaoMedicamentoExceptionCode.MPM_01195);
						}
					}
				}
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_FRM_DOS
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaFormaDosagem(Integer codigoMedicamento,
			Integer seqFormaDosagem) throws ApplicationBusinessException {
		DominioSituacao situacao = getFarmaciaFacade().obtemSituacaoFormaDosagem(seqFormaDosagem, codigoMedicamento);
		if(situacao == null) {
			throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_01132);
		}
		else if (!DominioSituacao.A.equals(situacao)) {
			throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_01131);
		}
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_ATU_SOLUCAO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void atualizaSolucao(Integer atdSeqPrescricaoMedicamento,
			Long seqPrescricaoMedicamento, Boolean autoExcluirProcedimentoMedicamentoSol) throws ApplicationBusinessException {
		
		// TODO Validar implementação, pois o correto seria que esta variavel
		// fosse inicializada como FALSE, então a validação para ver se é nula
		// estaria incorreta.
		// Validar e corrigir, caso necessário.
		if (autoExcluirProcedimentoMedicamentoSol == null
				|| !autoExcluirProcedimentoMedicamentoSol) {
			MpmPrescricaoMdto prescricaoMedicamento = getMpmPrescricaoMdtoDAO()
					.obterPorChavePrimaria(
							new MpmPrescricaoMdtoId(atdSeqPrescricaoMedicamento, seqPrescricaoMedicamento));
		
			if (prescricaoMedicamento != null) {
				Integer count = (prescricaoMedicamento.getItensPrescricaoMdtos() != null)?prescricaoMedicamento.getItensPrescricaoMdtos().size():0;

				if(prescricaoMedicamento.getDiluente() == null) {
					if (count > 1) {
						prescricaoMedicamento.setIndSolucao(true);
					} else {
						prescricaoMedicamento.setIndSolucao(false);
					}
				}

				getPrescricaoMedicamentoRN().alterarPrescricaoMedicamento(
						prescricaoMedicamento);
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_JUSTIF
	 * 
	 * @throws ApplicationBusinessException
	 */
//	public DominioIndPendenteItemPrescricao verificaJustificativa(Integer codigoMedicamento,
//			Integer jumSeq, Integer atdSeqPrescricaoMedica,
//			Long seqPrescricaoMedica) throws ApplicationBusinessException {
//
//		MpmPrescricaoMdto prescricaoMedicamento = getMpmPrescricaoMdtoDAO().obterMedicamentoPeloId(atdSeqPrescricaoMedica, seqPrescricaoMedica);
//		if(prescricaoMedicamento == null) {
//			throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_01422);
//		} else {
//			if(DominioIndPendenteItemPrescricao.N.equals(prescricaoMedicamento.getIndPendente())) {
//				AfaMedicamento medicamento = getFarmaciaFacade().obterMedicamento(codigoMedicamento);
//				if(medicamento.getAfaTipoUsoMdtos() != null && medicamento.getAfaTipoUsoMdtos().getIndExigeJustificativa()) {
//					if(jumSeq == null) {
//						throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_00851);
//					}
//				}
//				else {
//					if(jumSeq != null) {
//						throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_00852);
//					}					
//				}
//			}
//		}
//		
//		return prescricaoMedicamento.getIndPendente();
//	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_ULT_ITEM
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaUltimoItem(Integer pmdAtdSeq, Long pmdSeq, Boolean autoExcluirProcedimentoMedicamentoSol)
			throws ApplicationBusinessException {
		
		if (Boolean.FALSE
				.equals(autoExcluirProcedimentoMedicamentoSol)) {
			Long count = getMpmItemPrescricaoMdtoDAO()
					.pesquisarItensPrescricaoMdtosCount(pmdAtdSeq, pmdSeq, null,
							null);
			
			if (count == null || count == 0L) {
				// Não excluir o último item de prescrição de  medicamentos
				throw new ApplicationBusinessException(
						ItemPrescricaoMedicamentoExceptionCode.MPM_01277);
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_MDTO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaMedicamento(Integer codigoMedicamento)
			throws ApplicationBusinessException {

		AfaMedicamento medicamento = getFarmaciaFacade().obterMedicamento(codigoMedicamento);
		if(medicamento == null) {
			throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_01596);
		}
		else if(!DominioSituacaoMedicamento.A.equals(medicamento.getIndSituacao())) {
			throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_00846);
		}
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_OBS
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaExigeObservacao(Integer codigoMedicamento,
			String observacao) throws ApplicationBusinessException {
		// Esta regra destina a verificar se o ind_exige_observacao = 'S' para o
		// Medicamento relacionado então o campo observacao na tabela item
		// prescricao mdto deve ser not null
		AfaMedicamento medicamento = getFarmaciaFacade().obterMedicamento(codigoMedicamento);
		
		if (medicamento != null) {
			if (medicamento.getIndExigeObservacao() && observacao == null) {
				throw new ApplicationBusinessException(
						ItemPrescricaoMedicamentoExceptionCode.MPM_00794);
			} 
		}
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_PESO_KG
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaPesoKg(Integer seqAtendimento) throws ApplicationBusinessException {
		// Para informar qtde mg kg, exigir a existencia da informacao peso do
		// paciente cuja data criado_em seja a mais atual (max) e seja >= a data
		// do Atendimento a que a Prescricao Mdto esta relacionada
		AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(
				seqAtendimento);
		
		if (atendimento == null) {
			throw new ApplicationBusinessException(
					ItemPrescricaoMedicamentoExceptionCode.MPM_00072);
		} else {
			Date criadoEm = getPacienteFacade()
					.obterMaxDataCriadoEmPesoPaciente(
							atendimento.getPaciente().getCodigo());
			
			if (criadoEm == null) {
				throw new ApplicationBusinessException(
						ItemPrescricaoMedicamentoExceptionCode.MPM_01141);
			} else if (criadoEm.before(atendimento.getDthrInicio())) {
				throw new ApplicationBusinessException(
						ItemPrescricaoMedicamentoExceptionCode.MPM_01142);
			}
		}
	}

	

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_ATU_DEF_ORIG
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void atualizaDefaultOrigemJustificativa(MpmItemPrescricaoMdto itemPrescricaoMdto)
			throws ApplicationBusinessException {
		// Atualiza como default o valor 'N' (false)  para ind_origem_justif.
		itemPrescricaoMdto.setOrigemJustificativa(false);
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_PESO_SUP
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaPesoSuperficieCorporal(Integer seqAtendimento)
			throws ApplicationBusinessException {
		// Para informar qtde mg superficie corporal, exigir a existencia da
		// informacao peso do paciente cuja data criado_em seja a mais atual
		// (max) e seja >= a data do Atendimento a que a Prescricao Mdto esta
		// relacionada
		this.verificaPesoKg(seqAtendimento);
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_TB
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaGrupoUsoMedicamentoTuberculostatico(Integer pmdAtdSeq,
			Integer codigoMedicamento, Integer justificativaUsoMdtoSeq) throws ApplicationBusinessException {
		// Se o Medicamento relacionado pertence ao Grupo Uso Mdto
		// tuberculostatico e está sendo prescrito pela primeira vez,exigir
		// informacao Justificativa Tuberculostatico
		AghParametros aghParametros = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_GRPO_USO_MDTO_TB);
		Integer valorParametro = null;
		if (aghParametros != null
				&& aghParametros.getVlrNumerico() != null) {
			valorParametro = aghParametros.getVlrNumerico().intValue();
		} else {
			throw new ApplicationBusinessException(
					ItemPrescricaoMedicamentoExceptionCode.MPM_01148, Severity.INFO);
		}

		if (justificativaUsoMdtoSeq == null) {
			Long countTipoUsoMedicamento = getFarmaciaFacade()
					.listaTipoUsoMedicamentoPorMedicamentoEGupSeqCount(
							codigoMedicamento, valorParametro);
			
			if (countTipoUsoMedicamento != null && countTipoUsoMedicamento > 0) {
				Long countItensPrescricaoMedicamento = getMpmItemPrescricaoMdtoDAO().
				pesquisarItensPrescricaoMdtosCount(pmdAtdSeq, null, codigoMedicamento, null);
				
				if (countItensPrescricaoMedicamento != null 
						&& countItensPrescricaoMedicamento > 0) {				
					throw new ApplicationBusinessException(
							ItemPrescricaoMedicamentoExceptionCode.MPM_01149, Severity.INFO);
				}
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_ATEND
	 * 
	 * @throws ApplicationBusinessException
	 */
	public VerificaAtendimentoVO verificaAtendimento(Date dataHoraInicio,
			Date dataHoraFim, Integer seqAtendimento, Integer seqHospitalDia,
			Integer seqInternacao, Integer seqAtendimentoUrgencia)
			throws ApplicationBusinessException {
		return getPrescricaoMedicamentoRN().verificaAtendimento(dataHoraInicio,
				dataHoraFim, seqAtendimento, seqHospitalDia, seqInternacao,
				seqAtendimentoUrgencia);
	}

	/**
	 * ORADB Procedure MPMK_IME_RN.RN_IMEP_VER_CONV
	 * 
	 * @throws ApplicationBusinessException
	 */
//	public void verificaConvenio() throws ApplicationBusinessException {
//		// Exigir informacao de Justificativa Mdto Convenio se o Convenio
//		// relacionado ao Atendimento exige justificativa (Deve existir
//		// relacionamento entre convenios e medicamentos com indicacao de
//		// exigencia de justificativa)
//		throw new ApplicationBusinessException(
//				ItemPrescricaoMedicamentoExceptionCode.MPM_01206);
//	}
	
	/**
	 * ORADB Function MPMC_CALC_QTDE_24H
	 * 
	 * @param frequencia
	 * @param seqTipoFrequenciaAprazamento
	 * @param dosePrescrita
	 * @param seqFormaDosagem
	 * @param codigoMedicamento
	 * @return a quantidade 24 horas calculada
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public Short buscaCalculoQuantidade24Horas(Short frequencia,
			Short seqTipoFrequenciaAprazamento, BigDecimal dosePrescrita,
			Integer seqFormaDosagem, Integer codigoMedicamento) throws ApplicationBusinessException {

		//Ao atualizar este método, realizar a atualização correspondente
		//no método calcularValor24Horas da classe ConfirmarPrescricaoMedicaRN
		MpmTipoFrequenciaAprazamento tipoFreqAprazamento = seqTipoFrequenciaAprazamento != null ? getMpmTipoFrequenciaAprazamentoDAO().obterTipoFrequenciaAprazamentoPeloId(seqTipoFrequenciaAprazamento): null;
		if(tipoFreqAprazamento == null) {
			throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_01553);
		}
		
		AfaFormaDosagem formaDosagem = seqFormaDosagem != null ? getFarmaciaFacade().obterAfaFormaDosagem(seqFormaDosagem) : null;
		if(formaDosagem == null) {
			throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_02233);
		}
		
		AfaMedicamento medicamento = codigoMedicamento != null ? getFarmaciaFacade().obterMedicamento(codigoMedicamento): null;
		if(medicamento == null) {
			throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_02232);
		}
		
		BigDecimal doseUpPrescrita = dosePrescrita.divide(formaDosagem.getFatorConversaoUp(), 10, RoundingMode.FLOOR);
		if(!medicamento.getIndCalcDispensacaoFracionad()){//v_ind_calc_disp_frac = 'N'
			/*if(doseUpPrescrita.setScale(0, RoundingMode.FLOOR).compareTo(doseUpPrescrita) != BigDecimal.ZERO.intValue()){//diferente de zero, então diferentes
				doseUpPrescrita = doseUpPrescrita.add(BigDecimal.ONE).setScale(0, RoundingMode.FLOOR);
			}*/
			doseUpPrescrita = doseUpPrescrita.setScale(0, RoundingMode.UP);
		}
		BigDecimal dose24h;
		if(DominioFormaCalculoAprazamento.C.equals(tipoFreqAprazamento.getIndFormaAprazamento())){
			dose24h = doseUpPrescrita;
			/*if(dose24h.setScale(0, RoundingMode.FLOOR).compareTo(dose24h) != BigDecimal.ZERO.intValue()){
				dose24h = dose24h.add(BigDecimal.ONE).setScale(0, RoundingMode.FLOOR);
			}*/
			dose24h = dose24h.setScale(0, RoundingMode.UP);
			if(BigDecimal.ONE.intValue() == (dose24h.compareTo(new BigDecimal(9999)))){//if v_dose_24h > 9999
				throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_02197);
			}
			
			return dose24h.shortValue();
		}
		
		BigDecimal vQtde = null;
		if(DominioFormaCalculoAprazamento.I.equals(tipoFreqAprazamento.getIndFormaAprazamento())){
			if(frequencia == null) {
				throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MENSAGEM_NENHUMA_FREQUENCIA_APRAZAMENTO_ECONTRADA);
			}
			BigDecimal vezes = new BigDecimal(frequencia).multiply(tipoFreqAprazamento.getFatorConversaoHoras());
			if(BigDecimal.ONE.negate().intValue() == vezes.compareTo(BigDecimal.ONE)){// if v_vezes < 1
				return BigDecimal.ZERO.shortValue();
			}else
			{
				vQtde = new BigDecimal("24").divide(vezes, 10, RoundingMode.FLOOR).setScale(0, RoundingMode.FLOOR);
			}
		}
		
		if(DominioFormaCalculoAprazamento.V.equals(tipoFreqAprazamento.getIndFormaAprazamento())){
			if(BigDecimal.ONE.intValue() == tipoFreqAprazamento.getFatorConversaoHoras().compareTo(BigDecimal.ZERO)){//if v_fator_conversao_horas  > 0
				vQtde = new BigDecimal(frequencia).divide(tipoFreqAprazamento.getFatorConversaoHoras(), 10, RoundingMode.FLOOR).multiply(new BigDecimal("24"));
			}else{
				Long qtdeApraz = getMpmAprazamentoFrequenciasDAO().pesquisarQuantidadeTipoFreequencia(seqTipoFrequenciaAprazamento);
				if(qtdeApraz == null || qtdeApraz == 0) {
					qtdeApraz = 1l;
				}
				vQtde = new BigDecimal(qtdeApraz);
			}
		}
		dose24h = doseUpPrescrita.multiply(vQtde);
		/*if(dose24h.setScale(0, RoundingMode.FLOOR).compareTo(dose24h) != BigDecimal.ZERO.intValue()){
			dose24h = dose24h.add(BigDecimal.ONE).setScale(0, RoundingMode.FLOOR);
		}*/
		dose24h = dose24h.setScale(0, RoundingMode.UP);
		
		if(BigDecimal.ONE.intValue() == (dose24h.compareTo(new BigDecimal(9999)))){//if v_dose_24h > 9999
			throw new ApplicationBusinessException(ItemPrescricaoMedicamentoExceptionCode.MPM_01559);
		}
		
		return dose24h.shortValue();
		
	}
	
	
	/**
	 * Recebe uma lista de itens contendo o medicamento principal e o diluente
	 * de uma prescrição de medicamento e devole o medicamento principal.
	 * Este método tornou-se necessário após o desenvolvimento da melhoria #18867, em 
	 * que diluentes passaram a ser inseridos na tabela MPM_ITEM_PRESCRICAO_MDTOS da
	 * mesma forma que os itens de medicamentos
	 * @param listaItens
	 * @return
	 */
	public MpmItemPrescricaoMdto obterItemMedicamentoNaoDiluente(List<MpmItemPrescricaoMdto> listaItens){
		MpmItemPrescricaoMdto retorno = null;
		boolean diluenteEncontrado = false;
		MpmPrescricaoMdto medicamentoSolucao = listaItens.get(0).getPrescricaoMedicamento();		
		
		for(MpmItemPrescricaoMdto itemPrescricaoMedicamento : listaItens){
			//Ajuste que foi necessário devido à melhoria #18867
			if(!medicamentoSolucao.getIndSolucao()){
				if (medicamentoSolucao.getItensPrescricaoMdtos().size() > 1){
					if(itemPrescricaoMedicamento.getMedicamento().equals(medicamentoSolucao.getDiluente()) && !diluenteEncontrado) {
						diluenteEncontrado = true;
						continue;
					}
					else {
						retorno = itemPrescricaoMedicamento;
						break;
					}
				}
				else {
					retorno = listaItens.get(0);
				}
			}			
		}
		
		return retorno;
	}
	
	protected MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO(){
		return mpmTipoFrequenciaAprazamentoDAO;
	}
	
	protected MpmAprazamentoFrequenciasDAO getMpmAprazamentoFrequenciasDAO(){
		return mpmAprazamentoFrequenciasDAO;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	private IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected PrescricaoMedicamentoRN getPrescricaoMedicamentoRN() {
		return prescricaoMedicamentoRN;
	}

	protected MpmItemPrescParecerMdtoDAO getMpmItemPrescParecerMdtoDAO(){
		return mpmItemPrescParecerMdtoDAO;
	}

	protected MpmPrescricaoMdtoDAO getMpmPrescricaoMdtoDAO(){
		return mpmPrescricaoMdtoDAO;
	}

	protected MpmItemPrescricaoMdtoDAO getMpmItemPrescricaoMdtoDAO() {
		return mpmItemPrescricaoMdtoDAO;
	}
	
	protected MpmJustificativaUsoMdtoDAO getMpmJustificativaUsoMdtosDAO(){
		return mpmJustificativaUsoMdtoDAO;
	}

//	private ManterPrescricaoMedicamentoON getManterPrescricaoMedicamentoON() {
//		return manterPrescricaoMedicamentoON;
//	}

//	private PrescricaoMedicaRN getPrescricaoMedicaRN() {
//		return prescricaoMedicaRN;
//	}

}
