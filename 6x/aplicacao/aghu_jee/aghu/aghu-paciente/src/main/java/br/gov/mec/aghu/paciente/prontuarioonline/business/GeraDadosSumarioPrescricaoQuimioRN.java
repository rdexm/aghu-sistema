package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescricaoMedicamento;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.MptDataItemSumario;
import br.gov.mec.aghu.model.MptItemCuidadoSumario;
import br.gov.mec.aghu.model.MptItemCuidadoSumarioId;
import br.gov.mec.aghu.model.MptItemPrescricaoSumario;
import br.gov.mec.aghu.model.MptPrescricaoCuidado;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class GeraDadosSumarioPrescricaoQuimioRN  extends BaseBusiness {

	@EJB
	private GeraDadosSumPrescQuimioMdtoRN geraDadosSumPrescQuimioMdtoRN;
	
	@EJB
	private GeraDadosSumPrescQuimioSolRN geraDadosSumPrescQuimioSolRN;
	
	private static final Log LOG = LogFactory.getLog(GeraDadosSumarioPrescricaoQuimioRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	private static final long serialVersionUID = 5516216302754518545L;

	/**
	 * @ORADB MPTP_GERA_SUM_QUIMIO
	 * 
	 * Metodo para gerar dados em MptDataItemSumario
	 * 
	 * @param atdSeq
	 * @param pacCodigo
	 * @param dominioTipoEmissaoSumario
	 * @return dataInicioFimVO
	 */
	public void gerarDadosSumarioPrescricaoQuimioPOL(Integer atdSeq, Integer pacCodigo, 
			DominioTipoEmissaoSumario dominioTipoEmissaoSumario) throws ApplicationBusinessException {
		
		DominioTipoEmissaoSumario[] dominioTipoEmissao = {DominioTipoEmissaoSumario.I, DominioTipoEmissaoSumario.P, DominioTipoEmissaoSumario.C};

		if (Arrays.asList(dominioTipoEmissao).contains(dominioTipoEmissaoSumario)) {
		
			// @ORADB AGHC_GERA_ATD_PAC
			Integer apaSeq = getPacienteFacade().gerarAtendimentoPaciente(atdSeq); 

			List<MptPrescricaoPaciente> listaPrescricoesPaciente = getProcedimentoTerapeuticoFacade().pesquisarPrescricoesPacientePorAtendimento(atdSeq);
			for (MptPrescricaoPaciente prescricaoPaciente : listaPrescricoesPaciente) {
				if (apaSeq != null) {
					
					// @ORADB P_GERA_ITENS
					gerarItens(atdSeq, apaSeq, prescricaoPaciente);
				}
			}
		}		
	}
	
	/**
	 * @ORADB P_GERA_ITENS
	 * 
	 * Metodo para gerar dados em MptItemPrescricaoSumario, MptItemCuidadoSumario, MptItemMdtoSumario
	 * 
	 * @param atdSeq
	 * @param apaSeq
	 * @param prescricaoPaciente
	 * @throws ApplicationBusinessException 
	 */
	public void gerarItens(Integer atdSeq, Integer apaSeq, MptPrescricaoPaciente prescricaoPaciente) throws ApplicationBusinessException {
		 
		// @ORADB MPTP_GERA_SUM_CUID_Q (cuidados)
		mptpGeraSumCuidQ(atdSeq, apaSeq, prescricaoPaciente.getId().getAtdSeq(), prescricaoPaciente.getId().getSeq(), prescricaoPaciente.getDataPrevisaoExecucao());
		
		// @ORADB MPTP_GERA_SUM_MDTO_Q (medicamentos)
		getGeraDadosSumPrescQuimioMdtoRN().mptpGeraSumMdtoQ(atdSeq, apaSeq, prescricaoPaciente.getId().getAtdSeq(), prescricaoPaciente.getId().getSeq(), prescricaoPaciente.getDataPrevisaoExecucao());
		
		// @ORADB MPTP_GERA_S0l_Q (solucoes)
		getGeraDadosSumPrescQuimioSolRN().mptpGeraSumSolQ(atdSeq, apaSeq, prescricaoPaciente.getId().getAtdSeq(), prescricaoPaciente.getId().getSeq(), prescricaoPaciente.getDataPrevisaoExecucao());		
	}
	
	/**
	 * @ORADB MPTP_GERA_SUM_CUID_Q
	 * 
	 * Metodo para gerar dados em MptDataItemSumario
	 * 
	 * @param intAtdSeq (atdSeq)
	 * @param apaSeq
	 * @param pteAtdSeq
	 * @param pteSeq
	 * @param agpDtAgenda (dataPrevisaoExecucao)
	 * @throws ApplicationBusinessException 
	 */
	public void mptpGeraSumCuidQ(Integer intAtdSeq, Integer apaSeq, Integer pteAtdSeq, Integer pteSeq, Date agpDtAgenda) throws ApplicationBusinessException {
		
		List<MptPrescricaoCuidado> listaPrescricaoCuidado = getProcedimentoTerapeuticoFacade().pesquisarPrescricoesCuidado(pteAtdSeq, pteSeq, agpDtAgenda);
		processarListaPrescricaoCuidado(intAtdSeq, apaSeq, pteAtdSeq, pteSeq, agpDtAgenda, listaPrescricaoCuidado);
	}		
		
	protected void processarListaPrescricaoCuidado(Integer intAtdSeq, Integer apaSeq, Integer pteAtdSeq, Integer pteSeq, Date agpDtAgenda, 
			List<MptPrescricaoCuidado> listaPrescricaoCuidado) throws ApplicationBusinessException {		
		
		for (MptPrescricaoCuidado prescricaoCuidado : listaPrescricaoCuidado) {

			// @ORADB MPTP_SINT_SUM_CUID_Q (cuidados)			
			Integer itqSeq = mptSintSumCuidQ(intAtdSeq, apaSeq, pteAtdSeq, pteSeq, prescricaoCuidado.getId().getSeq());	

			if (itqSeq > 0){				
				Date data = agpDtAgenda;
				String valorP = "P";
				String valorS = null;
				if(prescricaoCuidado.getAlteradoEm() != null && DominioSituacaoItemPrescricaoMedicamento.V.equals(prescricaoCuidado.getIndSituacaoItem())){
					valorS = "S";
				}
				Integer seqp = 0;
				Boolean achouP = false;
				Boolean achouS = false;
				
				List<MptDataItemSumario> listaDataItemSumario = getProcedimentoTerapeuticoFacade().pesquisarDataItemSumario(intAtdSeq, apaSeq, itqSeq);
				for (MptDataItemSumario dataItemSumario : listaDataItemSumario) {
					seqp = dataItemSumario.getId().getSeqp();
					if( (valorP != null) && (valorP.equals(dataItemSumario.getValor())) && (DateUtil.truncaData(dataItemSumario.getData()) == data) ){
						achouP = true;						
					}
					if( (valorS != null) && (valorS.equals(dataItemSumario.getValor())) && (DateUtil.truncaData(dataItemSumario.getData()) == data) ){
						achouS = true;						
					}
				}
				if( !achouP && valorP != null){	
					seqp = seqp + 1;									
					
					//INSERT MptDataItemSumario 
					getGeraDadosSumPrescQuimioSolRN().inserirMptDataItemSumario(intAtdSeq, apaSeq, itqSeq, seqp, data, valorP);					
				}
				if( !achouS && valorS != null){	
					seqp = seqp + 1;									
					
					//INSERT MptDataItemSumario 
					getGeraDadosSumPrescQuimioSolRN().inserirMptDataItemSumario(intAtdSeq, apaSeq, itqSeq, seqp, data, valorS);						
				}
			}	
			identificarHierarquia(intAtdSeq, apaSeq, pteAtdSeq, pteSeq, prescricaoCuidado.getId().getSeq(), agpDtAgenda);				
		}
	}	

	private void identificarHierarquia(Integer intAtdSeq, Integer apaSeq, Integer pteAtdSeq, Integer pteSeq, Integer seq, Date agpDtAgenda) throws ApplicationBusinessException {
		List<MptPrescricaoCuidado> listaPrescricaoCuidadoHierarquia = getProcedimentoTerapeuticoFacade().listarPrescricaoCuidadoHierarquia(pteAtdSeq, pteSeq, seq, agpDtAgenda);

		if (listaPrescricaoCuidadoHierarquia != null && !listaPrescricaoCuidadoHierarquia.isEmpty()) {
			processarListaPrescricaoCuidado(intAtdSeq, apaSeq, pteAtdSeq, pteSeq, agpDtAgenda, listaPrescricaoCuidadoHierarquia);
		}			
	}

	/**
	 * @ORADB MPTP_SINT_SUM_CUID_Q
	 * 
	 * Metodo para gerar dados em MptItemPrescricaoSumario e MptItemCuidadoSumario (SINTAXE)
	 * 
	 * @param intAtdSeq
	 * @param apaSeq
	 * @param pteAtdSeq
	 * @param pteSeq
	 * @param pcoSeq
	 * @return itqSeq
	 * @throws ApplicationBusinessException 
	 */
	protected Integer mptSintSumCuidQ(Integer intAtdSeq, Integer apaSeq, Integer pteAtdSeq, Integer pteSeq, Integer pcoSeq) throws ApplicationBusinessException {
		String sintaxe = null;
		Integer itqSeq = null;
		
		MptPrescricaoCuidado prescricaoCuidado = getProcedimentoTerapeuticoFacade().obterMptPrescricaoCuidadoComJoin(pteAtdSeq, pteSeq, pcoSeq, Boolean.TRUE, Boolean.TRUE);
		if (prescricaoCuidado != null) {	
			TabIcs tabIcs = new TabIcs();
			if (prescricaoCuidado.getTipoFreqAprazamento() != null) {
				tabIcs.setIcsTfqSeq(prescricaoCuidado.getTipoFreqAprazamento().getSeq());
			} 

			tabIcs.setIcsCduSeq(prescricaoCuidado.getCuidadoUsual().getSeq());

			String descTfq = null;
			if (prescricaoCuidado.getTipoFreqAprazamento() != null) {
				if (prescricaoCuidado.getTipoFreqAprazamento().getSintaxe() != null && prescricaoCuidado.getFrequencia() != null) {
					descTfq = StringUtils.replace(prescricaoCuidado.getTipoFreqAprazamento().getSintaxe(), "#", prescricaoCuidado.getFrequencia().toString());
					tabIcs.setIcsFrequencia(prescricaoCuidado.getFrequencia());									
				}else{
					descTfq = prescricaoCuidado.getTipoFreqAprazamento().getDescricao();
				}
			}

			if (prescricaoCuidado.getComplemento() != null) {
				tabIcs.setIcsDescricao(prescricaoCuidado.getComplemento());

				if (prescricaoCuidado.getCuidadoUsual().getIndOutros()) {
					sintaxe = prescricaoCuidado.getComplemento();					
				}else{
					sintaxe = getAmbulatorioFacade().mpmcMinusculo(
							prescricaoCuidado.getCuidadoUsual().getDescricao(), 1).concat(" ".concat(prescricaoCuidado.getComplemento()));	
				}				
			}else{
				if (prescricaoCuidado.getCuidadoUsual().getIndOutros()) {
					sintaxe = " ";
				}else{
					sintaxe = getAmbulatorioFacade().mpmcMinusculo(prescricaoCuidado.getCuidadoUsual().getDescricao(), 1);	
				}				
			}

			sintaxe = sintaxe.concat(" ".concat(descTfq)); 			
			
			DominioTipoItemPrescricaoSumario dominioTipoItemPrescSum = DominioTipoItemPrescricaoSumario.POSITIVO_4;
			itqSeq = 0;
			
			List<MptItemPrescricaoSumario> listaItemPrescricaoSumario = getProcedimentoTerapeuticoFacade().pesquisarItemPrescricaoSumario(intAtdSeq, apaSeq, sintaxe, dominioTipoItemPrescSum);
			if(listaItemPrescricaoSumario.isEmpty()){				
				itqSeq = inserirItemPrescSumEItemCuidSum(intAtdSeq, apaSeq, sintaxe, dominioTipoItemPrescSum, tabIcs);	
			}
		}
		return itqSeq;
	}
	
	private Integer inserirItemPrescSumEItemCuidSum(Integer intAtdSeq, Integer apaSeq, String sintaxe, 
			DominioTipoItemPrescricaoSumario dominioTipoItemPrescSum, TabIcs tabIcs) throws ApplicationBusinessException {
				
		//INSERT MptItemPrescricaoSumario
		Integer itemPrescSumSeq = inserirMptItemPrescricaoSumario(intAtdSeq, apaSeq, sintaxe, dominioTipoItemPrescSum);				
		
		//INSERT MptItemCuidadoSumario
		inserirMptItemCuidadoSumario(tabIcs, itemPrescSumSeq );	
		
		return itemPrescSumSeq;	
	}

	//INSERT MptItemPrescricaoSumario
	private Integer inserirMptItemPrescricaoSumario(Integer intAtdSeq, Integer apaSeq, String sintaxe, DominioTipoItemPrescricaoSumario dominioTipoItemPrescSum) throws ApplicationBusinessException {
			
		MptItemPrescricaoSumario itemPrescSum = new MptItemPrescricaoSumario();

		//TODO lilian.silveira ################# TESTAR COMENTANDO METODO ABAIXO ############################## 
		//getProcedimentoTerapeuticoFacade().obterValorSequencialIdMptItemPrescricaoSumario(itemPrescSum);	
		
		itemPrescSum.setAghAtendimentoPacientes(getAghuFacade().obterAtendimentoPaciente(intAtdSeq, apaSeq));
		itemPrescSum.setDescricao(sintaxe);
		itemPrescSum.setTipo(dominioTipoItemPrescSum.getCodigo());
		
		getProcedimentoTerapeuticoFacade().persistirMptItemPrescricaoSumario(itemPrescSum);	
		
		return itemPrescSum.getSeq();
	}
	
	//INSERT MptItemCuidadoSumario
	private void inserirMptItemCuidadoSumario(TabIcs tabIcs, Integer itemPrescSumSeq) {
		MptItemCuidadoSumario itemCuidSum = new MptItemCuidadoSumario();
		MptItemCuidadoSumarioId itemCuidSumId = new MptItemCuidadoSumarioId(itemPrescSumSeq, 1);
		
		itemCuidSum.setId(itemCuidSumId); 
		itemCuidSum.setMpmCuidadoUsual(getCadastrosBasicosPrescricaoMedicaFacade().obterCuidadoUsualPorChavePrimaria(tabIcs.getIcsCduSeq())); 
		itemCuidSum.setMpmTipoFrequenciaAprazamento(getPrescricaoMedicaFacade().obterTipoFrequeciaAprazPorChavePrimaria(tabIcs.getIcsTfqSeq())); 
		itemCuidSum.setFrequencia(tabIcs.getIcsFrequencia()); 
		itemCuidSum.setDescricao(tabIcs.getIcsDescricao());		
		
		getProcedimentoTerapeuticoFacade().persistirMptItemCuidadoSumario(itemCuidSum);	
	}

	class TabIcs{
		
		private Short icsTfqSeq;
		private Integer icsFrequencia;
		private String icsDescricao;
		private Integer icsCduSeq;
		
		
		public Short getIcsTfqSeq() {
			return icsTfqSeq;
		}
		public Integer getIcsFrequencia() {
			return icsFrequencia;
		}
		public String getIcsDescricao() {
			return icsDescricao;
		}
		public Integer getIcsCduSeq() {
			return icsCduSeq;
		}
		public void setIcsTfqSeq(Short icsTfqSeq) {
			this.icsTfqSeq = icsTfqSeq;
		}
		public void setIcsFrequencia(Integer icsFrequencia) {
			this.icsFrequencia = icsFrequencia;
		}
		public void setIcsDescricao(String icsDescricao) {
			this.icsDescricao = icsDescricao;
		}
		public void setIcsCduSeq(Integer icsCduSeq) {
			this.icsCduSeq = icsCduSeq;
		}			
	}		
	  
	
	private GeraDadosSumPrescQuimioMdtoRN getGeraDadosSumPrescQuimioMdtoRN(){
		return geraDadosSumPrescQuimioMdtoRN;
	}
	
	protected GeraDadosSumPrescQuimioSolRN getGeraDadosSumPrescQuimioSolRN(){
		return geraDadosSumPrescQuimioSolRN;
	}

	protected IPacienteFacade getPacienteFacade() {
		return this.pacienteFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected ICadastrosBasicosPrescricaoMedicaFacade getCadastrosBasicosPrescricaoMedicaFacade() {
		return this.cadastrosBasicosPrescricaoMedicaFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return this.prescricaoMedicaFacade;
	}
	
	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}
	
	protected IFarmaciaApoioFacade getFarmaciaApoioFacade() {
		return this.farmaciaApoioFacade;
	}	
	
}
