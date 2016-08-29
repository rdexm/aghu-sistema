package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.math.RoundingMode;
import java.util.ArrayList;
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
import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;
import br.gov.mec.aghu.dominio.DominioUnidadeCorrer;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.MptDataItemSumario;
import br.gov.mec.aghu.model.MptDataItemSumarioId;
import br.gov.mec.aghu.model.MptItemMdtoSumario;
import br.gov.mec.aghu.model.MptItemMdtoSumarioId;
import br.gov.mec.aghu.model.MptItemPrescricaoMedicamento;
import br.gov.mec.aghu.model.MptItemPrescricaoSumario;
import br.gov.mec.aghu.model.MptPrescricaoMedicamento;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItemMdtoSumarioVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class GeraDadosSumPrescQuimioSolRN  extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(GeraDadosSumPrescQuimioSolRN.class);

	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
		
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
		
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;	
	
	
	private static final long serialVersionUID = 6527489963811348915L;

	/**
	 * @ORADB MPTP_GERA_SUM_SOL_Q
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
	public void mptpGeraSumSolQ(Integer intAtdSeq, Integer apaSeq, Integer pteAtdSeq, Integer pteSeq, Date agpDtAgenda) throws ApplicationBusinessException {

		Boolean solucao = true;
		List<MptPrescricaoMedicamento> listaPrescricaoMedicamento = getProcedimentoTerapeuticoFacade().listarPrescricoesMedicamento(pteAtdSeq, pteSeq, agpDtAgenda, solucao);
		
		processarListaPrescricaoMedicamento(intAtdSeq, apaSeq, pteAtdSeq, pteSeq, agpDtAgenda, listaPrescricaoMedicamento, solucao);
	}
		
	public void processarListaPrescricaoMedicamento(Integer intAtdSeq, Integer apaSeq, Integer pteAtdSeq, Integer pteSeq, Date agpDtAgenda, 
			List<MptPrescricaoMedicamento> listaPrescricaoMedicamento, Boolean solucao ) throws ApplicationBusinessException {
		
		for (MptPrescricaoMedicamento prescricaoMedicamento : listaPrescricaoMedicamento) { 

			// @ORADB MPTP_SINT_SUM_SOL_Q (solucoes)			
			Integer itqSeq = mptSintSumSolQ(intAtdSeq, apaSeq, pteAtdSeq, pteSeq, prescricaoMedicamento.getId().getSeq());	

			if (itqSeq > 0){				
				Date data = agpDtAgenda;
				String valorP = "P";
				String valorS = null;
				if(prescricaoMedicamento.getAlteradoEm() != null && DominioSituacaoItemPrescricaoMedicamento.V.equals(prescricaoMedicamento.getSituacaoItem())){
					valorS = "S";
				}
				Integer seqp = 0;
				Boolean achouP = false;
				Boolean achouS = false;

				List<MptDataItemSumario> listaDataItemSumario = getProcedimentoTerapeuticoFacade().pesquisarDataItemSumario(intAtdSeq, apaSeq, itqSeq);
				for (MptDataItemSumario dataItemSumario : listaDataItemSumario) {
					seqp = dataItemSumario.getId().getSeqp();
					if( (valorP != null) && (dataItemSumario.getValor() == valorP) && (DateUtil.truncaData(dataItemSumario.getData()) == data) ){
						achouP = true;						
					}
					if( (valorS != null) && (dataItemSumario.getValor() == valorS) && (DateUtil.truncaData(dataItemSumario.getData()) == data) ){
						achouS = true;						
					}
				}
				if( !achouP && valorP != null){
					seqp = seqp + 1;	

					//INSERT MptDataItemSumario 
					inserirMptDataItemSumario(intAtdSeq, apaSeq, itqSeq, seqp, data, valorP);					
				}
				if( !achouS && valorS != null){
					seqp = seqp + 1;					

					//INSERT MptDataItemSumario 
					inserirMptDataItemSumario(intAtdSeq, apaSeq, itqSeq, seqp, data, valorS);						
				}
			}				
			
			identificarHierarquia(intAtdSeq, apaSeq, pteAtdSeq, pteSeq, prescricaoMedicamento.getId().getSeq(), agpDtAgenda, solucao);	
		}
	}
	
	private void identificarHierarquia(Integer intAtdSeq, Integer apaSeq, Integer pteAtdSeq, Integer pteSeq, Integer seq, Date agpDtAgenda,
			Boolean solucao) throws ApplicationBusinessException {
		
		List<MptPrescricaoMedicamento> listaPrescricaoMedicamentoHierarquia = getProcedimentoTerapeuticoFacade().listarPrescricaoMedicamentoHierarquia(pteAtdSeq, pteSeq, seq, agpDtAgenda, solucao);
		
		if (listaPrescricaoMedicamentoHierarquia != null && !listaPrescricaoMedicamentoHierarquia.isEmpty()) {
			processarListaPrescricaoMedicamento(intAtdSeq, apaSeq, pteAtdSeq, pteSeq, agpDtAgenda, listaPrescricaoMedicamentoHierarquia, solucao);
		}			
	}

	//INSERT MptDataItemSumario 
	public void inserirMptDataItemSumario(Integer intAtdSeq, Integer apaSeq, Integer itqSeq, Integer seqp, Date data, String valor) {
		//seqp = seqp + 1;

		MptDataItemSumario itemSum = new MptDataItemSumario();
		MptDataItemSumarioId idItemSum = new MptDataItemSumarioId(intAtdSeq, apaSeq, itqSeq, seqp);					

		itemSum.setId(idItemSum);					
		itemSum.setData(data);
		itemSum.setValor(valor);
		getProcedimentoTerapeuticoFacade().persistirMptDataItemSumario(itemSum);		
	}	

	/**
	 * @ORADB MPTP_SINT_SUM_SOL_Q
	 * 
	 * Metodo para gerar dados em MptItemPrescricaoSumario e MptItemMdtoSumario (SINTAXE) 
	 * 
	 * @param intAtdSeq
	 * @param apaSeq
	 * @param pteAtdSeq
	 * @param pteSeq
	 * @param pmoSeq
	 * @return itqSeq
	 * @throws ApplicationBusinessException 
	 */
	public Integer mptSintSumSolQ(Integer intAtdSeq, Integer apaSeq, Integer pteAtdSeq, Integer pteSeq, Integer pmoSeq) throws ApplicationBusinessException {		

		Integer itqSeq = null;				
		Boolean solucao = true;		 

		MptPrescricaoMedicamento prescricaoMedicamento = getProcedimentoTerapeuticoFacade().obterMptPrescricaoMedicamentoComJoin(pteAtdSeq, pteSeq, pmoSeq, solucao);
		if (prescricaoMedicamento != null) {
			
			List<ItemMdtoSumarioVO> listaTabIms = new ArrayList<ItemMdtoSumarioVO>();	
			StringBuilder sintaxeSol = new StringBuilder("");
			Boolean temFilho = false;
			Boolean primeiraVez = true;
			Integer cont = 0;

			List<MptItemPrescricaoMedicamento> listaItemPrescricaoMedicamento = getProcedimentoTerapeuticoFacade().listarItensPrescricaoMdtoJoin(pteAtdSeq, pteSeq, pmoSeq);
			for (MptItemPrescricaoMedicamento itemPrescricaoMedicamento : listaItemPrescricaoMedicamento) {

				ItemMdtoSumarioVO tabImsSol = new ItemMdtoSumarioVO();
				temFilho = true;
				cont = cont + 1;		

				if( !primeiraVez ){
					sintaxeSol.append('\n');				
				}else{
					primeiraVez = false;
				}

				setarDadosSintaxeEItemMdtoSumarioVO(prescricaoMedicamento, itemPrescricaoMedicamento, sintaxeSol, tabImsSol);	

				setarDadosItemMdtoSumarioVO(prescricaoMedicamento, itemPrescricaoMedicamento, tabImsSol);

				listaTabIms.add(tabImsSol);
			}

			if(temFilho){				
				setarDadosSintaxe(prescricaoMedicamento, sintaxeSol);		
			}

			//Integer limImsSol = cont;			

			String sintaxe = sintaxeSol.toString();
			DominioTipoItemPrescricaoSumario dominioTipoItemPrescSum = DominioTipoItemPrescricaoSumario.POSITIVO_8;		
			Boolean indSolucao = true;
			itqSeq = 0;

			List<MptItemPrescricaoSumario> listaItemPrescricaoSumario = getProcedimentoTerapeuticoFacade().pesquisarItemPrescricaoSumario(intAtdSeq, apaSeq, sintaxe, dominioTipoItemPrescSum);
			if(listaItemPrescricaoSumario.isEmpty() && !sintaxe.isEmpty() ){
				itqSeq = inserirItemPrescSumEItemMdtoSum(intAtdSeq, apaSeq, sintaxe, dominioTipoItemPrescSum, listaTabIms, indSolucao );			
			}	
		}			
		return itqSeq;
	}	

	// inserirItemPrescSumEItemMdtoSum
	public Integer inserirItemPrescSumEItemMdtoSum(Integer intAtdSeq, Integer apaSeq, String sintaxe,
			DominioTipoItemPrescricaoSumario dominioTipoItemPrescSum, List<ItemMdtoSumarioVO> listaTabIms, Boolean indSolucao) throws ApplicationBusinessException {

		//INSERT MptItemPrescricaoSumario
		Integer itemPrescSumSeq = inserirMptItemPrescricaoSumario(intAtdSeq, apaSeq, sintaxe, dominioTipoItemPrescSum);				

		//INSERT MptItemMdtoSumario
		inserirMptItemMdtoSumario(listaTabIms, itemPrescSumSeq, indSolucao);

		return itemPrescSumSeq;		
	}
	
	//INSERT MptItemPrescricaoSumario
	private Integer inserirMptItemPrescricaoSumario(Integer intAtdSeq, Integer apaSeq, String sintaxeSumrCuid, DominioTipoItemPrescricaoSumario dominioTipoItemPrescSum) throws ApplicationBusinessException {

		MptItemPrescricaoSumario itemPrescSum = new MptItemPrescricaoSumario();

		//TODO lilian.silveira ################# TESTAR COMENTANDO METODO ABAIXO ############################## 
		//getProcedimentoTerapeuticoFacade().obterValorSequencialIdMptItemPrescricaoSumario(itemPrescSum);	
		
		itemPrescSum.setAghAtendimentoPacientes(getAghuFacade().obterAtendimentoPaciente(intAtdSeq, apaSeq));
		itemPrescSum.setDescricao(sintaxeSumrCuid);
		itemPrescSum.setTipo(dominioTipoItemPrescSum.getCodigo());  

		getProcedimentoTerapeuticoFacade().persistirMptItemPrescricaoSumario(itemPrescSum);	
		
		return itemPrescSum.getSeq();
	}

	//INSERT MptItemMdtoSumario 
	private void inserirMptItemMdtoSumario(List<ItemMdtoSumarioVO> listaTabIms, Integer itemPrescSumSeq, Boolean indSolucao) {

		Integer imsSeq = 0;

		for (ItemMdtoSumarioVO tabImsSol : listaTabIms) {
			imsSeq = imsSeq + 1;

			MptItemMdtoSumario itemMdtoSum = new MptItemMdtoSumario();
			MptItemMdtoSumarioId itemMdtoSumId = new MptItemMdtoSumarioId(itemPrescSumSeq, imsSeq);

			itemMdtoSum.setId(itemMdtoSumId); 
			itemMdtoSum.setMpmTipoFrequenciaAprazamento(getPrescricaoMedicaFacade().obterTipoFrequeciaAprazPorChavePrimaria(tabImsSol.getImsTfqSeq()));
			itemMdtoSum.setAfaMedicamentoByMedMatCodigo(getFarmaciaFacade().obterMedicamento(tabImsSol.getImsMatCodigo()));
			itemMdtoSum.setAfaFormaDosagem(getFarmaciaFacade().obterAfaFormaDosagem(tabImsSol.getImsFdsSeq())); 
			itemMdtoSum.setAfaViaAdministracao(getFarmaciaFacade().obterViaAdministracao(tabImsSol.getImsVadSigla())); 			
			itemMdtoSum.setIndSolucao(indSolucao);
			
			if(tabImsSol.getImsTvaSeq() != null){
				itemMdtoSum.setAfaTipoVelocAdministracoes(getFarmaciaFacade().obterAfaTipoVelocAdministracoes(tabImsSol.getImsTvaSeq()));
			}	
			if(tabImsSol.getImsFrequecia() != null){
				itemMdtoSum.setFrequencia(tabImsSol.getImsFrequecia());	
			}
			
			setarMptItemMdtoSumario(itemMdtoSum, tabImsSol);			
			
			getProcedimentoTerapeuticoFacade().persistirMptItemMdtoSumario(itemMdtoSum);				
		}
	}

	private void setarMptItemMdtoSumario(MptItemMdtoSumario itemMdtoSum, ItemMdtoSumarioVO tabImsSol) {				
		
		if(tabImsSol.getImsHoraInicioAdministracao() != null){
			itemMdtoSum.setHoraInicioAdministracao(tabImsSol.getImsHoraInicioAdministracao());
		}
		if(tabImsSol.getImsQtdeCorrer() != null){
			itemMdtoSum.setQtdeHorasCorrer(tabImsSol.getImsQtdeCorrer().shortValue());	
		}
		if(tabImsSol.getImsUnidadeCorrer() != null){
			itemMdtoSum.setUnidHorasCorrer(tabImsSol.getImsUnidadeCorrer());
		}
		if(tabImsSol.getImsGotejo() != null){
			itemMdtoSum.setGotejo(tabImsSol.getImsGotejo().floatValue());
		}
		if(tabImsSol.getImsIndSeNecessario() != null){
			itemMdtoSum.setIndSeNecessario(tabImsSol.getImsIndSeNecessario());
		}
		if(tabImsSol.getImsObservacao() != null){
			itemMdtoSum.setObservacao(tabImsSol.getImsObservacao());
		}
		if(tabImsSol.getImsObservacaoItem() != null){
			itemMdtoSum.setObservacaoItem(tabImsSol.getImsObservacaoItem());
		}
		if(tabImsSol.getImsDose() != null){
			itemMdtoSum.setDose(tabImsSol.getImsDose().doubleValue()); 
		}			
		if(tabImsSol.getImsDiasUsoDomiciliar() != null){
			itemMdtoSum.setDiasUsoDomiciliar(tabImsSol.getImsDiasUsoDomiciliar().shortValue());		
		}		
	}

	private void setarDadosSintaxe(MptPrescricaoMedicamento prescricaoMedicamento, StringBuilder sintaxeSol) {

		sintaxeSol.append('\n');

		if (prescricaoMedicamento.getViaAdministracao() != null){
			sintaxeSol.append(prescricaoMedicamento.getViaAdministracao().getSigla());
		}

		String descTfq = null;
		if(prescricaoMedicamento.getTipoFreqAprazamento() != null){ 
			if(prescricaoMedicamento.getTipoFreqAprazamento().getSintaxe() != null && prescricaoMedicamento.getFrequencia() != null){
				descTfq = StringUtils.replace(prescricaoMedicamento.getTipoFreqAprazamento().getSintaxe(), "#", prescricaoMedicamento.getFrequencia().toString());
			}
		}else{
			descTfq = prescricaoMedicamento.getTipoFreqAprazamento().getDescricao();					
		}

		sintaxeSol.append(", ").append(descTfq);

		if (prescricaoMedicamento.getHoraInicioAdministracao() != null){
			sintaxeSol.append(", ").append("I=").append(DateUtil.dataToString(prescricaoMedicamento.getHoraInicioAdministracao(), "HH:mm")).append(" h ");
		}

		setarOutrosDadosSintaxe(prescricaoMedicamento, sintaxeSol);		
	}

	private void setarOutrosDadosSintaxe(MptPrescricaoMedicamento prescricaoMedicamento, StringBuilder sintaxeSol) {

		if (prescricaoMedicamento.getQtdeCorrer() != null){
			if (prescricaoMedicamento.getHoraInicioAdministracao() != null){
				sintaxeSol.append(", ");
			}

			sintaxeSol.append(", Correr em ").append(prescricaoMedicamento.getQtdeCorrer());

			if (prescricaoMedicamento.getUnidadeCorrer() == null || DominioUnidadeCorrer.H.equals(prescricaoMedicamento.getUnidadeCorrer())){
				sintaxeSol.append(" horas");						
			}else{
				sintaxeSol.append(" minutos");
			}					
		}

		if (prescricaoMedicamento.getGotejo() != null){
			sintaxeSol.append(", ").append("Gotejo ");
			if (prescricaoMedicamento.getGotejo().stripTrailingZeros().scale() <= 0){
				sintaxeSol.append(prescricaoMedicamento.getGotejo());
			}else{
				sintaxeSol.append(prescricaoMedicamento.getGotejo().setScale(2, RoundingMode.FLOOR).stripTrailingZeros().toString().replace('.' , ','));	
			}			

			if (prescricaoMedicamento.getTipoVelocidadeAdministracao() != null){
				sintaxeSol.append(' ').append(prescricaoMedicamento.getTipoVelocidadeAdministracao().getDescricao());
			}
		}

		if (prescricaoMedicamento.getSeNecessario()){
			sintaxeSol.append(", se necessario");					
		}

		if (prescricaoMedicamento.getDiasDeUsoDomiciliar() != null){
			sintaxeSol.append(", Uso domiciliar por ").append(prescricaoMedicamento.getDiasDeUsoDomiciliar()).append(" dias ");					
		}

		if (prescricaoMedicamento.getObservacao() != null){
			sintaxeSol.append('\n');
			sintaxeSol.append("Obs.: ").append(prescricaoMedicamento.getObservacao());	
		}		
	}

	private void setarDadosItemMdtoSumarioVO(MptPrescricaoMedicamento prescricaoMedicamento, MptItemPrescricaoMedicamento itemPrescricaoMedicamento,
			ItemMdtoSumarioVO tabImsSol) {

		if (prescricaoMedicamento.getFrequencia() != null){
			tabImsSol.setImsFrequecia(prescricaoMedicamento.getFrequencia());
		}

		if (prescricaoMedicamento.getHoraInicioAdministracao() != null){
			tabImsSol.setImsHoraInicioAdministracao(prescricaoMedicamento.getHoraInicioAdministracao());
		}

		if (prescricaoMedicamento.getQtdeCorrer() != null){
			tabImsSol.setImsQtdeCorrer(prescricaoMedicamento.getQtdeCorrer());
		}

		if (prescricaoMedicamento.getGotejo() != null){ 	
			if (prescricaoMedicamento.getGotejo().stripTrailingZeros().scale() <= 0){
				tabImsSol.setImsGotejo(prescricaoMedicamento.getGotejo());
			}else{
				tabImsSol.setImsGotejo(prescricaoMedicamento.getGotejo().setScale(2, RoundingMode.FLOOR).stripTrailingZeros());
			}			
		}

		if (prescricaoMedicamento.getSeNecessario()){
			tabImsSol.setImsIndSeNecessario(true);
		}else{
			tabImsSol.setImsIndSeNecessario(false);
		}

		if (prescricaoMedicamento.getObservacao() != null){
			tabImsSol.setImsObservacao(prescricaoMedicamento.getObservacao());
		}

		if (prescricaoMedicamento.getViaAdministracao() != null){
			tabImsSol.setImsVadSigla(prescricaoMedicamento.getViaAdministracao().getSigla());
		}

		if (prescricaoMedicamento.getUnidadeCorrer() != null){
			tabImsSol.setImsUnidadeCorrer(prescricaoMedicamento.getUnidadeCorrer());
		}

		if (prescricaoMedicamento.getDiasDeUsoDomiciliar() != null){
			tabImsSol.setImsDiasUsoDomiciliar(prescricaoMedicamento.getDiasDeUsoDomiciliar());
		}			
	}

	private void setarDadosSintaxeEItemMdtoSumarioVO(MptPrescricaoMedicamento prescricaoMedicamento, MptItemPrescricaoMedicamento itemPrescricaoMedicamento,
			StringBuilder sintaxeSol, ItemMdtoSumarioVO tabImsSol) {

		sintaxeSol.append(getAmbulatorioFacade().mpmcMinusculo(itemPrescricaoMedicamento.getMedicamento().getDescricao(), 1)); 

		if (itemPrescricaoMedicamento.getMedicamento().getConcentracao() != null){ 
			sintaxeSol.append(": ");
			if (itemPrescricaoMedicamento.getMedicamento().getConcentracao().stripTrailingZeros().scale() <= 0){
				sintaxeSol.append(itemPrescricaoMedicamento.getMedicamento().getConcentracao().toString());
			}else{
				sintaxeSol.append(itemPrescricaoMedicamento.getMedicamento().getConcentracao().setScale(4, RoundingMode.FLOOR).stripTrailingZeros().toString().replace('.' , ','));
			}												
		}

		if (itemPrescricaoMedicamento.getMedicamento().getMpmUnidadeMedidaMedicas() != null){					
			sintaxeSol.append(' ').append(itemPrescricaoMedicamento.getMedicamento().getMpmUnidadeMedidaMedicas().getDescricao().toLowerCase()); 				
		}

		if (itemPrescricaoMedicamento.getComplementoNp() != null){	
			tabImsSol.setImsObservacaoItem(itemPrescricaoMedicamento.getComplementoNp());	
			sintaxeSol.append(" : ").append(itemPrescricaoMedicamento.getComplementoNp().toLowerCase()); 				
		}

		if (itemPrescricaoMedicamento.getDose() != null){ 			
			if (itemPrescricaoMedicamento.getDose().stripTrailingZeros().scale() <= 0){
				tabImsSol.setImsDose(itemPrescricaoMedicamento.getDose());				
			}else{
				tabImsSol.setImsDose(itemPrescricaoMedicamento.getDose().setScale(4, RoundingMode.FLOOR).stripTrailingZeros());				
			}			
			sintaxeSol.append(" - Administrar ");			
			sintaxeSol.append(tabImsSol.getImsDose().toString().replace('.' , ','));									
		}	

		if (prescricaoMedicamento.getTipoFreqAprazamento() != null){
			tabImsSol.setImsTfqSeq(prescricaoMedicamento.getTipoFreqAprazamento().getSeq());
		}
		if (prescricaoMedicamento.getTipoVelocidadeAdministracao() != null){
			tabImsSol.setImsTvaSeq(prescricaoMedicamento.getTipoVelocidadeAdministracao().getSeq());
		}

		tabImsSol.setImsMatCodigo(itemPrescricaoMedicamento.getMedicamento().getMatCodigo());

		if (itemPrescricaoMedicamento.getFormaDosagem() != null){ 
			tabImsSol.setImsFdsSeq(itemPrescricaoMedicamento.getFormaDosagem().getSeq());
			if(itemPrescricaoMedicamento.getFormaDosagem().getUnidadeMedidaMedicas() != null){					
				sintaxeSol.append(' ');
				sintaxeSol.append(itemPrescricaoMedicamento.getFormaDosagem().getUnidadeMedidaMedicas().getDescricao().toLowerCase()); 				
			}else{
				if (itemPrescricaoMedicamento.getMedicamento().getTipoApresentacaoMedicamento() != null){	
					sintaxeSol.append(' ');
					sintaxeSol.append(itemPrescricaoMedicamento.getMedicamento().getTipoApresentacaoMedicamento().getSigla().toLowerCase()); 
				}
			}
		}	
	}

	
	private IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	private IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}

	private IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	private IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return this.prescricaoMedicaFacade;
	}

	private IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

}