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
public class GeraDadosSumPrescQuimioMdtoRN  extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(GeraDadosSumPrescQuimioMdtoRN.class);

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
	/**
	 * Gera dados sumário prescrição quimio MEDICAMENTOS
	 */
	private static final long serialVersionUID = -9130305701776292719L;

	/**
	 * @ORADB MPTP_GERA_SUM_MDTO_Q
	 * 
	 * Metodo para gerar dados em MptItemMdtoSumario
	 * 
	 * @param intAtdSeq
	 * @param apaSeq
	 * @param pteAtdSeq
	 * @param pteSeq
	 * @param agpDtAgenda
	 * @throws ApplicationBusinessException 
	 */
	public void mptpGeraSumMdtoQ(Integer intAtdSeq, Integer apaSeq, Integer pteAtdSeq, Integer pteSeq, Date agpDtAgenda) throws ApplicationBusinessException {
		
		Boolean solucao = false;
		List<MptPrescricaoMedicamento> listaPrescricaoMedicamento = getProcedimentoTerapeuticoFacade().listarPrescricoesMedicamento(pteAtdSeq, pteSeq, agpDtAgenda, solucao);
		
		processarListaPrescricaoMedicamento(intAtdSeq, apaSeq, pteAtdSeq, pteSeq, agpDtAgenda, listaPrescricaoMedicamento, solucao);
		
	}
		
	public void processarListaPrescricaoMedicamento(Integer intAtdSeq, Integer apaSeq, Integer pteAtdSeq, Integer pteSeq, Date agpDtAgenda, 
			List<MptPrescricaoMedicamento> listaPrescricaoMedicamento, Boolean solucao ) throws ApplicationBusinessException {
		
		
		for (MptPrescricaoMedicamento prescricaoMedicamento : listaPrescricaoMedicamento) {

			// @ORADB MPTP_SINT_SUM_MDTO_Q (medicamentos)
			Integer itqSeq = mptSintSumMdtoQ(intAtdSeq, apaSeq, pteAtdSeq, pteSeq, prescricaoMedicamento.getId().getSeq());
			
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
					String valor = valorP;
					
					//INSERT MptDataItemSumario 
					inserirMptDataItemSumario(intAtdSeq, apaSeq, itqSeq, seqp, data, valor);					
				}
				if( !achouS && valorS != null){
					seqp = seqp + 1;
					String valor = valorS;
					
					//INSERT MptDataItemSumario 
					inserirMptDataItemSumario(intAtdSeq, apaSeq, itqSeq, seqp, data, valor);						
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
		
		MptDataItemSumario itemSum = new MptDataItemSumario();
		MptDataItemSumarioId idItemSum = new MptDataItemSumarioId(intAtdSeq, apaSeq, itqSeq, seqp);					

		itemSum.setId(idItemSum);					
		itemSum.setData(data);
		itemSum.setValor(valor);
		
		getProcedimentoTerapeuticoFacade().persistirMptDataItemSumario(itemSum);		
	}	

	/**
	 * @ORADB MPTP_SINT_SUM_MDTO_Q
	 * 
	 * Metodo para gerar dados de SINTAXE de Medicamentos
	 * 
	 * @param intAtdSeq
	 * @param apaSeq
	 * @param pteAtdSeq
	 * @param pteSeq
	 * @param pmoSeq
	 * @return itqSeq
	 * @throws ApplicationBusinessException 
	 */
	public Integer mptSintSumMdtoQ(Integer intAtdSeq, Integer apaSeq, Integer pteAtdSeq, Integer pteSeq, Integer pmoSeq) throws ApplicationBusinessException  {
		
		Integer itqSeq = null;
		Boolean solucao = false;
		
		MptPrescricaoMedicamento prescricaoMedicamento = getProcedimentoTerapeuticoFacade().obterMptPrescricaoMedicamentoComJoin(pteAtdSeq, pteSeq, pmoSeq, solucao);
		if (prescricaoMedicamento != null) {
			
			List<ItemMdtoSumarioVO> listaTabIms = new ArrayList<ItemMdtoSumarioVO>();
			StringBuilder sintaxeMdto = new StringBuilder(" ");
			Boolean temFilho = false;
			Boolean primeiraVez = true;
			Integer cont = 0;
			
			List<MptItemPrescricaoMedicamento> listaItemPrescricaoMedicamento = getProcedimentoTerapeuticoFacade().listarItensPrescricaoMedicamento(intAtdSeq, pteSeq, pmoSeq);
			for(MptItemPrescricaoMedicamento itemPrescricaoMedicamento : listaItemPrescricaoMedicamento){
					
				ItemMdtoSumarioVO tabIms = new ItemMdtoSumarioVO();
				temFilho = true;
				cont = cont + 1;		
				
				if(!primeiraVez){
					sintaxeMdto.append('\n');				
				}else{
					primeiraVez = false;
				}
				carregarDadosItemPrescricaoMdtoPrimeiraParte(prescricaoMedicamento, itemPrescricaoMedicamento, sintaxeMdto, tabIms);
				carregarDadosItemPrescricaoMdtoSegundaParte(prescricaoMedicamento, itemPrescricaoMedicamento, sintaxeMdto, tabIms);
				carregarDadosItemPrescricaoMdtoTerceiraParte(prescricaoMedicamento, itemPrescricaoMedicamento, sintaxeMdto, tabIms);
				listaTabIms.add(tabIms);	
				}
				if(temFilho){				
					carregarDadosPrescricaoMdtoPrimeiraParte(prescricaoMedicamento, sintaxeMdto);	
					carregarDadosPrescricaoMdtoSegundaParte(prescricaoMedicamento, sintaxeMdto);	
				}
				
								
				String sintaxe = sintaxeMdto.toString();
				DominioTipoItemPrescricaoSumario dominioTipoItemPrescSum = DominioTipoItemPrescricaoSumario.POSITIVO_6;	
				Boolean indSolucao = false;
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

		itemPrescSum.setAghAtendimentoPacientes(getAghuFacade().obterAtendimentoPaciente(intAtdSeq, apaSeq));
		itemPrescSum.setDescricao(sintaxeSumrCuid);
		itemPrescSum.setTipo(dominioTipoItemPrescSum.getCodigo());

		getProcedimentoTerapeuticoFacade().persistirMptItemPrescricaoSumario(itemPrescSum);	
		
		return itemPrescSum.getSeq();
	}
	
	//INSERT MptItemMdtoSumario 	
	private void inserirMptItemMdtoSumario(List<ItemMdtoSumarioVO> listaTabIms, Integer itemPrescSumSeq, Boolean indSolucao) {
		
		Integer imsSeq = 0;

		for (ItemMdtoSumarioVO tabIms : listaTabIms) {
			imsSeq = imsSeq + 1;
			
			MptItemMdtoSumario itemMdtoSum = new MptItemMdtoSumario();
			MptItemMdtoSumarioId itemMdtoSumId = new MptItemMdtoSumarioId(itemPrescSumSeq, imsSeq);
			
			itemMdtoSum.setId(itemMdtoSumId); 
			itemMdtoSum.setMpmTipoFrequenciaAprazamento(getPrescricaoMedicaFacade().obterTipoFrequeciaAprazPorChavePrimaria(tabIms.getImsTfqSeq()));
			itemMdtoSum.setAfaMedicamentoByMedMatCodigo(getFarmaciaFacade().obterMedicamento(tabIms.getImsMatCodigo()));
			itemMdtoSum.setAfaFormaDosagem(getFarmaciaFacade().obterAfaFormaDosagem(tabIms.getImsFdsSeq()));
			itemMdtoSum.setAfaViaAdministracao(getFarmaciaFacade().obterViaAdministracao(tabIms.getImsVadSigla()));	
			itemMdtoSum.setIndSolucao(indSolucao);
			
			if(tabIms.getImsTvaSeq() != null){
				itemMdtoSum.setAfaTipoVelocAdministracoes(getFarmaciaFacade().obterAfaTipoVelocAdministracoes(tabIms.getImsTvaSeq()));	
			}
			if(tabIms.getImsFrequecia() != null){
				itemMdtoSum.setFrequencia(tabIms.getImsFrequecia());	
			}
			
			setarMptItemMdtoSumario(itemMdtoSum, tabIms);		
			
			
			getProcedimentoTerapeuticoFacade().persistirMptItemMdtoSumario(itemMdtoSum);		
		}
	}
	
	private void setarMptItemMdtoSumario(MptItemMdtoSumario itemMdtoSum, ItemMdtoSumarioVO tabIms) {		

		if(tabIms.getImsHoraInicioAdministracao() != null){
			itemMdtoSum.setHoraInicioAdministracao(tabIms.getImsHoraInicioAdministracao());
		}
		if (tabIms.getImsQtdeCorrer() != null){
			itemMdtoSum.setQtdeHorasCorrer(tabIms.getImsQtdeCorrer().shortValue());	
		}
		if(tabIms.getImsUnidadeCorrer() != null){
			itemMdtoSum.setUnidHorasCorrer(tabIms.getImsUnidadeCorrer());
		}
		if (tabIms.getImsGotejo() != null){
			itemMdtoSum.setGotejo(tabIms.getImsGotejo().floatValue());
		}
		if(tabIms.getImsIndSeNecessario() != null){
			itemMdtoSum.setIndSeNecessario(tabIms.getImsIndSeNecessario());
		}
		if(tabIms.getImsObservacao() != null){
			itemMdtoSum.setObservacao(tabIms.getImsObservacao());
		}
		if(tabIms.getImsObservacaoItem() != null){
			itemMdtoSum.setObservacaoItem(tabIms.getImsObservacaoItem());
		}
		if (tabIms.getImsDose() != null){
			itemMdtoSum.setDose(tabIms.getImsDose().doubleValue());
		}
		
		if (tabIms.getImsDiasUsoDomiciliar() != null){
			itemMdtoSum.setDiasUsoDomiciliar(tabIms.getImsDiasUsoDomiciliar().shortValue());		
		}		
	}
	
	
	/**
	 * Método para carregar a PRIMEIRAPARTE dos dados referentes aos itens 
	 * (OBS: *** para atender o tamanho estabelecido no PMD foi necessário subdividir um método em vários ***)
	 * @param prescricaoMedicamento
	 * @param itemPrescricaoMedicamento
	 * @param sintaxeMdto
	 * @param tabIms
	 */
	private void carregarDadosItemPrescricaoMdtoPrimeiraParte(MptPrescricaoMedicamento prescricaoMedicamento, 
		MptItemPrescricaoMedicamento itemPrescricaoMedicamento, StringBuilder sintaxeMdto, ItemMdtoSumarioVO tabIms) {
		sintaxeMdto.append(getAmbulatorioFacade().mpmcMinusculo(itemPrescricaoMedicamento.getMedicamento().getDescricao(), 1)); 
		if (itemPrescricaoMedicamento.getMedicamento() != null) {
			tabIms.setImsMatCodigo(itemPrescricaoMedicamento.getMedicamento().getMatCodigo());
			if (itemPrescricaoMedicamento.getMedicamento().getConcentracao() != null){
				sintaxeMdto.append(": ");
				if (itemPrescricaoMedicamento.getMedicamento().getConcentracao().stripTrailingZeros().scale() <= 0){
					sintaxeMdto.append(itemPrescricaoMedicamento.getMedicamento().getConcentracao().toString());
				}else{
					sintaxeMdto.append(itemPrescricaoMedicamento.getMedicamento().getConcentracao().setScale(4, RoundingMode.FLOOR).stripTrailingZeros().toString().replace('.' , ','));
				}
			}
			if (itemPrescricaoMedicamento.getMedicamento().getMpmUnidadeMedidaMedicas() != null){					
				sintaxeMdto.append(' ').append( itemPrescricaoMedicamento.getMedicamento().getMpmUnidadeMedidaMedicas().getDescricao().toLowerCase()); 				
			}
		}
		if (itemPrescricaoMedicamento.getComplementoNp() != null){	
			tabIms.setImsObservacaoItem(itemPrescricaoMedicamento.getComplementoNp());	
			sintaxeMdto.append(" : " ).append( itemPrescricaoMedicamento.getComplementoNp().toLowerCase()); 				
		}
		if (itemPrescricaoMedicamento.getDose() != null){			
			if (itemPrescricaoMedicamento.getDose().stripTrailingZeros().scale() <= 0){
				tabIms.setImsDose(itemPrescricaoMedicamento.getDose());				
			}else{
				tabIms.setImsDose(itemPrescricaoMedicamento.getDose().setScale(4, RoundingMode.FLOOR).stripTrailingZeros());				
			}			
			sintaxeMdto.append(" - Administrar ");
			sintaxeMdto.append(tabIms.getImsDose().toString().replace('.' , ','));						
		}	
	}
	
	/**
	 * Método para carregar a SEGUNDA PARTE dos dados referentes aos itens 
	 * (OBS: *** para atender o tamanho estabelecido no PMD foi necessário subdividir um método em vários ***)
	 * @param prescricaoMedicamento
	 * @param itemPrescricaoMedicamento
	 * @param sintaxeMdto
	 * @param tabIms
	 */
	private void carregarDadosItemPrescricaoMdtoSegundaParte(MptPrescricaoMedicamento prescricaoMedicamento, 
		MptItemPrescricaoMedicamento itemPrescricaoMedicamento, StringBuilder sintaxeMdto, ItemMdtoSumarioVO tabIms) {
		//sintaxeMdto.append(getAmbulatorioFacade().mpmcMinusculo(itemPrescricaoMedicamento.getMedicamento().getDescricao(), 1)); 
		if (prescricaoMedicamento.getTipoFreqAprazamento() != null && prescricaoMedicamento.getTipoFreqAprazamento().getSeq() != null){
				tabIms.setImsTfqSeq(prescricaoMedicamento.getTipoFreqAprazamento().getSeq());	
		}
		if (prescricaoMedicamento.getTipoVelocidadeAdministracao() != null && prescricaoMedicamento.getTipoVelocidadeAdministracao().getSeq() != null){
			tabIms.setImsTvaSeq(prescricaoMedicamento.getTipoVelocidadeAdministracao().getSeq());
		}
		
		if (itemPrescricaoMedicamento.getFormaDosagem() != null){
			if (itemPrescricaoMedicamento.getFormaDosagem().getSeq() != null){
				tabIms.setImsFdsSeq(itemPrescricaoMedicamento.getFormaDosagem().getSeq());
			}
			if(itemPrescricaoMedicamento.getFormaDosagem().getUnidadeMedidaMedicas() != null){					
				sintaxeMdto.append(' ');
				sintaxeMdto.append(itemPrescricaoMedicamento.getFormaDosagem().getUnidadeMedidaMedicas().getDescricao().toLowerCase()); 				
			}else{
				if (itemPrescricaoMedicamento.getMedicamento() != null){	
					if (itemPrescricaoMedicamento.getMedicamento().getTipoApresentacaoMedicamento() != null){
						sintaxeMdto.append(' ');
						sintaxeMdto.append(itemPrescricaoMedicamento.getMedicamento().getTipoApresentacaoMedicamento().getSigla().toLowerCase()); 
					}
				}
			}
		}
	}
	

	/**
	 * Método para carregar a TERCEIRA PARTE dos dados referentes aos itens 
	 * (OBS: *** para atender o tamanho estabelecido no PMD foi necessário subdividir um método em vários ***)
	 * @param prescricaoMedicamento
	 * @param itemPrescricaoMedicamento
	 * @param sintaxeMdto
	 * @param tabIms
	 */
	private void carregarDadosItemPrescricaoMdtoTerceiraParte(MptPrescricaoMedicamento prescricaoMedicamento,
			MptItemPrescricaoMedicamento itemPrescricaoMedicamento, StringBuilder sintaxeMdto, ItemMdtoSumarioVO tabIms) {
		if(prescricaoMedicamento.getFrequencia() != null){
			tabIms.setImsFrequecia(prescricaoMedicamento.getFrequencia());
		}
		if(prescricaoMedicamento.getHoraInicioAdministracao() != null){
		tabIms.setImsHoraInicioAdministracao(prescricaoMedicamento.getHoraInicioAdministracao());
		}
		if(prescricaoMedicamento.getQtdeCorrer() != null){
			tabIms.setImsQtdeCorrer(prescricaoMedicamento.getQtdeCorrer());
		}
		if(prescricaoMedicamento.getGotejo() != null){
			if (prescricaoMedicamento.getGotejo().stripTrailingZeros().scale() <= 0){
				tabIms.setImsGotejo(prescricaoMedicamento.getGotejo());
			}else{
				tabIms.setImsGotejo(prescricaoMedicamento.getGotejo().setScale(2, RoundingMode.FLOOR).stripTrailingZeros());
			}
		}
		tabIms.setImsIndSeNecessario(prescricaoMedicamento.getSeNecessario());
		if(prescricaoMedicamento.getObservacao() != null){
			tabIms.setImsObservacao(prescricaoMedicamento.getObservacao());
		}
		if(prescricaoMedicamento.getViaAdministracao().getSigla() != null){
			tabIms.setImsVadSigla(prescricaoMedicamento.getViaAdministracao().getSigla());
		}
		
		if(prescricaoMedicamento.getUnidadeCorrer() != null){
			tabIms.setImsUnidadeCorrer(prescricaoMedicamento.getUnidadeCorrer());
			
		}
		if(prescricaoMedicamento.getDiasDeUsoDomiciliar() != null){
		tabIms.setImsDiasUsoDomiciliar(prescricaoMedicamento.getDiasDeUsoDomiciliar());
		}
	}
	
	/**
	 * Método para carregar a PRIMEIRA PARTE dos dados referentes a prescricao de medicamentos   
	 * @param prescricaoMedicamento
	 * @param sintaxeMdto
	 */
	private void carregarDadosPrescricaoMdtoPrimeiraParte(MptPrescricaoMedicamento prescricaoMedicamento,StringBuilder sintaxeMdto) {
		sintaxeMdto.append(" , ");
		
		if (prescricaoMedicamento.getViaAdministracao() != null){
			sintaxeMdto.append(prescricaoMedicamento.getViaAdministracao().getSigla());
		}
		
		String descTfq = null;
		if (prescricaoMedicamento.getTipoFreqAprazamento() != null) {
           if (prescricaoMedicamento.getTipoFreqAprazamento().getSintaxe() != null){
			descTfq = StringUtils.replace(prescricaoMedicamento.getTipoFreqAprazamento().getSintaxe(), "#", prescricaoMedicamento.getFrequencia().toString());
           }
          else{
			descTfq = prescricaoMedicamento.getTipoFreqAprazamento().getDescricao();					
          }
		}
		sintaxeMdto.append(", ").append(descTfq);
		
		if (prescricaoMedicamento.getHoraInicioAdministracao() != null){
			sintaxeMdto.append(", ").append("I=").append(DateUtil.dataToString(prescricaoMedicamento.getHoraInicioAdministracao(), "HH:mm")).append(" h ");
		}
		if (prescricaoMedicamento.getQtdeCorrer() != null){
			if (prescricaoMedicamento.getHoraInicioAdministracao() != null){
				sintaxeMdto.append(", ");
			}
								
			sintaxeMdto.append(", Correr em ").append(prescricaoMedicamento.getQtdeCorrer());
			
			if (prescricaoMedicamento.getUnidadeCorrer() == null || DominioUnidadeCorrer.H.equals(prescricaoMedicamento.getUnidadeCorrer())){
				sintaxeMdto.append(" horas");						
			}else{
				sintaxeMdto.append(" minutos");
			}					
		}
	}
	
	/**
	 * Método para carregar a SEGUNDA PARTE dos dados referentes a prescricao de medicamentos   
	 * @param prescricaoMedicamento
	 * @param sintaxeMdto
	 */
	private void carregarDadosPrescricaoMdtoSegundaParte(MptPrescricaoMedicamento prescricaoMedicamento,StringBuilder sintaxeMdto) {
		if (prescricaoMedicamento.getGotejo() != null){
			sintaxeMdto.append(", ").append("Gotejo ");
			if (prescricaoMedicamento.getGotejo().stripTrailingZeros().scale() <= 0){
				sintaxeMdto.append(prescricaoMedicamento.getGotejo());
			}else{
				sintaxeMdto.append(prescricaoMedicamento.getGotejo().setScale(2, RoundingMode.FLOOR).stripTrailingZeros().toString().replace('.' , ','));
			}			

			if (prescricaoMedicamento.getTipoVelocidadeAdministracao() != null){
				sintaxeMdto.append(' ').append(getAmbulatorioFacade().mpmcMinusculo(prescricaoMedicamento.getTipoVelocidadeAdministracao().getDescricao(), 1));
			}
		}
		if (prescricaoMedicamento.getSeNecessario()){
			sintaxeMdto.append(", se necessario");					
		}
		if (prescricaoMedicamento.getObservacao() != null){
			//sintaxeMdto.append('\n');
			sintaxeMdto.append(". Obs.: ").append(prescricaoMedicamento.getObservacao());	
		}		
		if (prescricaoMedicamento.getDiasDeUsoDomiciliar() != null){
			sintaxeMdto.append(", Uso domiciliar por ").append(prescricaoMedicamento.getDiasDeUsoDomiciliar()).append(" dias ");					
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