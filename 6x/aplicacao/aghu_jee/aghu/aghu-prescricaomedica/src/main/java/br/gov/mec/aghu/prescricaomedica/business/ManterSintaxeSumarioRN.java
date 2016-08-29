package br.gov.mec.aghu.prescricaomedica.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioOrigemPrescricao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioUnidadeCorrer;
import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentoPacientesId;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNpt;
import br.gov.mec.aghu.model.MpmItemConsultoriaSumario;
import br.gov.mec.aghu.model.MpmItemConsultoriaSumarioId;
import br.gov.mec.aghu.model.MpmItemCuidadoSumario;
import br.gov.mec.aghu.model.MpmItemCuidadoSumarioId;
import br.gov.mec.aghu.model.MpmItemDietaSumario;
import br.gov.mec.aghu.model.MpmItemDietaSumarioId;
import br.gov.mec.aghu.model.MpmItemHemoterapiaSumario;
import br.gov.mec.aghu.model.MpmItemHemoterapiaSumarioId;
import br.gov.mec.aghu.model.MpmItemMdtoSumario;
import br.gov.mec.aghu.model.MpmItemMdtoSumarioId;
import br.gov.mec.aghu.model.MpmItemNptSumario;
import br.gov.mec.aghu.model.MpmItemNptSumarioId;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoNpt;
import br.gov.mec.aghu.model.MpmItemPrescricaoSumario;
import br.gov.mec.aghu.model.MpmItemProcedimentoSumario;
import br.gov.mec.aghu.model.MpmItemProcedimentoSumarioId;
import br.gov.mec.aghu.model.MpmModoUsoPrescProced;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoDietaId;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoriaId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmComposicaoPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemConsultoriaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemCuidadoSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemDietaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemHemoterapiaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemMdtoSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemNptSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemProcedimentoSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModoUsoPrescProcedDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * ORADB MPMK_SINTAXE_SUMARIO
 * 
 * @author dlaks
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class ManterSintaxeSumarioRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterSintaxeSumarioRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmPrescricaoNptDAO mpmPrescricaoNptDAO;

@EJB
private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;

@Inject
private MpmItemCuidadoSumarioDAO mpmItemCuidadoSumarioDAO;

@Inject
private MpmItemPrescricaoDietaDAO mpmItemPrescricaoDietaDAO;

@Inject
private MpmItemPrescricaoNptDAO mpmItemPrescricaoNptDAO;

@Inject
private MpmModoUsoPrescProcedDAO mpmModoUsoPrescProcedDAO;

@Inject
private MpmItemNptSumarioDAO mpmItemNptSumarioDAO;

@Inject
private MpmItemHemoterapiaSumarioDAO mpmItemHemoterapiaSumarioDAO;

@Inject
private MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO;

@Inject
private MpmItemDietaSumarioDAO mpmItemDietaSumarioDAO;

@EJB
private IBancoDeSangueFacade bancoDeSangueFacade;

@Inject
private MpmItemMdtoSumarioDAO mpmItemMdtoSumarioDAO;

@Inject
private MpmItemProcedimentoSumarioDAO mpmItemProcedimentoSumarioDAO;

@Inject
private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;

@EJB
private IAghuFacade aghuFacade;

@Inject
private MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO;

@Inject
private MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO;

@Inject
private MpmItemConsultoriaSumarioDAO mpmItemConsultoriaSumarioDAO;

@Inject
private MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtoDAO;

@Inject
private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;

@Inject
private MpmComposicaoPrescricaoNptDAO mpmComposicaoPrescricaoNptDAO;

@Inject
private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 237744720471218002L;


	/**
	 * ORADB Procedure MPMK_SINTAXE_SUMARIO.MPMP_SINT_SUMR_DIET
	 * 
	 * Esta rotina monta a sintaxe do sumario do item das dietas médicas.
	 * 
	 * @throws ApplicationBusinessException
	 *  
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public Integer montaSintaxeSumarioItemDietasMedicas(Integer seqAtendimento,
			Integer seqAtendimentoPaciente, Long seqPrescricaoDieta) throws ApplicationBusinessException {
		MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO = getMpmPrescricaoDietaDAO();
		MpmItemPrescricaoDietaDAO mpmItemPrescricaoDietaDAO = getMpmItemPrescricaoDietaDAO();
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmItemDietaSumarioDAO mpmItemDietaSumarioDAO = getMpmItemDietaSumarioDAO();
		
		List<MpmItemDietaSumario> listItemDietaSumario = new ArrayList<MpmItemDietaSumario>();
		int ituSeq=0;
		StringBuilder sintaxePrcrDietas = new StringBuilder();		
		
		MpmPrescricaoDieta prescricaoDieta = mpmPrescricaoDietaDAO.obterPorChavePrimaria(new MpmPrescricaoDietaId(seqAtendimento, seqPrescricaoDieta));
		if (prescricaoDieta != null) {
			int count=0;
			for (MpmItemPrescricaoDieta ipd : mpmItemPrescricaoDietaDAO.obterItensPrescricaoDieta(prescricaoDieta)){
				count+=1;

				MpmItemDietaSumario ids=new MpmItemDietaSumario();
				
				if (sintaxePrcrDietas.length() != 0) {
					sintaxePrcrDietas.append('\n');
				}
				sintaxePrcrDietas.append(StringUtils.capitalize(ipd.getTipoItemDieta().getDescricao().toLowerCase()));
				if (ipd.getQuantidade()!=null) {
					sintaxePrcrDietas.append(' ');
					sintaxePrcrDietas.append(ipd.getQuantidade() ).append(' ');
					sintaxePrcrDietas.append(StringUtils.capitalize((ipd.getTipoItemDieta() != null && ipd.getTipoItemDieta().getUnidadeMedidaMedica() != null) ? ipd.getTipoItemDieta().getUnidadeMedidaMedica().getDescricao().toLowerCase(): ""));
					ids.setQuantidade(ipd.getQuantidade());
				}
				if (ipd.getTipoFreqAprazamento() != null && ipd.getTipoFreqAprazamento().getSintaxe()!=null){
					sintaxePrcrDietas.append(ipd.getTipoFreqAprazamento().getSintaxeFormatada(ipd.getFrequencia())).append(' ');
					ids.setFrequencia(ipd.getFrequencia());
				} else if (ipd.getTipoFreqAprazamento() != null && ipd.getTipoFreqAprazamento().getDescricao()!=null){
					sintaxePrcrDietas = new StringBuilder("\n" + sintaxePrcrDietas.toString() + 
							" " + ipd.getTipoFreqAprazamento().getDescricao());
				}
				if (ipd.getNumVezes()!=null){
					sintaxePrcrDietas.append(", número de vezes: ");
					sintaxePrcrDietas.append(ipd.getNumVezes());
					ids.setNumVezes(ipd.getNumVezes());
				}
				if (prescricaoDieta.getObservacao()!=null){
					ids.setObservacao(prescricaoDieta.getObservacao());
				}
				
				MpmItemDietaSumarioId idsId=new MpmItemDietaSumarioId();
				idsId.setSeqp(count);
				ids.setId(idsId);
				
				ids.setTipoFreqAprazamento(ipd.getTipoFreqAprazamento());
				ids.setTipoItemDieta(ipd.getTipoItemDieta());
				ids.setIndAvalNutricionista(prescricaoDieta.getIndAvalNutricionista());				
				ids.setIndBombaInfusao(prescricaoDieta.getIndBombaInfusao());
				listItemDietaSumario.add(ids);
			}

			if (prescricaoDieta.getIndAvalNutricionista()) {
				sintaxePrcrDietas.append('\n');
				sintaxePrcrDietas.append("Avaliação Nutricionista");
			}
			if (prescricaoDieta.getIndBombaInfusao() != null
					&& prescricaoDieta.getIndBombaInfusao()) {
				sintaxePrcrDietas.append("BI" + " ");
			}
			if (prescricaoDieta.getIndAvalNutricionista() != null
					&& prescricaoDieta.getIndAvalNutricionista()) {
				sintaxePrcrDietas.append(", ");
			}
	
			MpmItemPrescricaoSumario ips = new MpmItemPrescricaoSumario();
			ips.setAtendimentoPaciente(getAghuFacade().obterAtendimentoPaciente(seqAtendimento, seqAtendimentoPaciente));
			ips.setTipo(DominioTipoItemPrescricaoSumario.POSITIVO_2);
			ips.setOrigemPrescricao(DominioOrigemPrescricao.I);
			ips.setDescricao(sintaxePrcrDietas.toString());
			
			if (!sintaxePrcrDietas.toString().isEmpty()){
				List<MpmItemPrescricaoSumario> lista = mpmItemPrescricaoSumarioDAO.listarItensPrescricaoSumario(seqAtendimento,
								seqAtendimentoPaciente, sintaxePrcrDietas.toString(),DominioTipoItemPrescricaoSumario.POSITIVO_2);
				if (lista == null || lista.isEmpty()) {
					mpmItemPrescricaoSumarioDAO.persistir(ips);
					mpmItemPrescricaoSumarioDAO.flush();					
					ituSeq = ips.getSeq();	
					for (MpmItemDietaSumario ids : listItemDietaSumario){
						ids.setItemPrescricaoSumario(ips);
						ids.getId().setItuSeq(ituSeq);
						
						ids.setItemPrescricaoSumario(ips);
					 
						mpmItemDietaSumarioDAO.persistir(ids);
						mpmItemDietaSumarioDAO.flush();
					}
				} else {
					ituSeq = lista.get(0).getSeq();
				}
			}	
		}
		return ituSeq;
	}

	/**
	 * ORADB Procedure MPMK_SINTAXE_SUMARIO.MPMP_SINT_SUMR_CUID
	 * 
	 * Esta rotina monta a sintaxe do sumario do item dos cuidados médicos.
	 * 
	 * @throws ApplicationBusinessException
	 *  
	 */
	public Integer montaSintaxeSumarioItemCuidadosMedicos(
			Integer seqAtendimento, Integer seqAtendimentoPaciente,
			Long seqPrescricaoCuidado) throws ApplicationBusinessException {
		MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO = getMpmPrescricaoCuidadoDAO();
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmItemCuidadoSumarioDAO mpmItemCuidadoSumarioDAO = getMpmItemCuidadoSumarioDAO();
		
		List<MpmItemCuidadoSumario> listItemCuidadoSumario = new ArrayList<MpmItemCuidadoSumario>();
		int ituSeq=0;
		int count=0;
		String descTfq = null;
		StringBuffer sintaxeSumrCuid = null;
		
		for (MpmPrescricaoCuidado prescricaoCuidado : mpmPrescricaoCuidadoDAO.listPrescricaoCuidadoPorId(seqAtendimento, seqPrescricaoCuidado)){
			
			count+=1;
			MpmItemCuidadoSumario ics=new MpmItemCuidadoSumario();				
			
			MpmItemCuidadoSumarioId id= new MpmItemCuidadoSumarioId();
			id.setSeqp(count);
			ics.setId(id);
			ics.setTipoFreqAprazamento(prescricaoCuidado.getMpmTipoFreqAprazamentos());
			ics.setCuidadoUsual(prescricaoCuidado.getMpmCuidadoUsuais());
			
			if (prescricaoCuidado.getMpmTipoFreqAprazamentos().getSintaxe()!=null){
				descTfq = prescricaoCuidado.getMpmTipoFreqAprazamentos().getSintaxeFormatada(prescricaoCuidado.getFrequencia().shortValue())+ " ";
				ics.setFrequencia(prescricaoCuidado.getFrequencia());
			}else{
				descTfq = prescricaoCuidado.getMpmTipoFreqAprazamentos().getDescricao() + " " ;
			}
			if (prescricaoCuidado.getDescricao()!=null){
				ics.setDescricao(prescricaoCuidado.getDescricao());
				if (prescricaoCuidado.getMpmCuidadoUsuais().getIndOutros()){
					sintaxeSumrCuid = new StringBuffer(prescricaoCuidado.getDescricao()).append(' ');
				}else{
					sintaxeSumrCuid = new StringBuffer(prescricaoCuidado.getMpmCuidadoUsuais().getDescricao().toLowerCase())
											.append(' ')
											.append(prescricaoCuidado.getDescricao())
											.append(' ');
				}
			}else{
				if (prescricaoCuidado.getMpmCuidadoUsuais().getIndOutros()){
					sintaxeSumrCuid = new StringBuffer();
				}else{
					sintaxeSumrCuid = new StringBuffer(prescricaoCuidado.getMpmCuidadoUsuais().getDescricao()).append(' ');
				}
			}
			sintaxeSumrCuid.append(' ').append(descTfq);
			listItemCuidadoSumario.add(ics);
		}
		
		MpmItemPrescricaoSumario ips = new MpmItemPrescricaoSumario();
		ips.setAtendimentoPaciente(getAghuFacade().obterAtendimentoPaciente(seqAtendimento, seqAtendimentoPaciente));
		ips.setTipo(DominioTipoItemPrescricaoSumario.POSITIVO_4);
		ips.setOrigemPrescricao(DominioOrigemPrescricao.I);
		ips.setDescricao(sintaxeSumrCuid.toString());
		
		List<MpmItemPrescricaoSumario> lista = mpmItemPrescricaoSumarioDAO
				.listarItensPrescricaoSumario(seqAtendimento,
						seqAtendimentoPaciente, sintaxeSumrCuid.toString(),
						DominioTipoItemPrescricaoSumario.POSITIVO_4);
		if (lista == null || lista.isEmpty()) {	
			mpmItemPrescricaoSumarioDAO.persistir(ips);
			mpmItemPrescricaoSumarioDAO.flush();
			ituSeq = ips.getSeq();	
			for (MpmItemCuidadoSumario ics : listItemCuidadoSumario){
				ics.setItemPrescricaoSumario(ips);
				ics.getId().setItuSeq(ituSeq);
				ics.setItemPrescricaoSumario(ips);
				
				mpmItemCuidadoSumarioDAO.persistir(ics);
				mpmItemCuidadoSumarioDAO.flush();
			}
		} else {
			ituSeq = lista.get(0).getSeq();
		}
		return ituSeq;
	}

	/**
	 * ORADB Procedure MPMK_SINTAXE_SUMARIO.MPMP_SINT_SUMR_MDTO
	 * 
	 * Esta rotina monta a sintaxe do sumario do item dos medicamentos.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public Integer montaSintaxeSumarioItemMedicamento(Integer seqAtendimento,
			Integer seqAtendimentoPaciente, Long seqPrescricaoMedicamento)
			throws ApplicationBusinessException {
		MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO = getMpmPrescricaoMdtoDAO();
		MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtoDAO = getMpmItemPrescricaoMdtoDAO();
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmItemMdtoSumarioDAO mpmItemMdtoSumarioDAO = getMpmItemMdtoSumarioDAO();
		
		Boolean temFilho = false;
		Boolean primeiraVez = true;
		Boolean incluiuItu = false;
		Integer ituSeq = 0;
		StringBuilder sintaxeMdto = new StringBuilder("");
		HashMap<Integer, MpmItemMdtoSumario> itemMdtoSumarioMap = new HashMap<Integer, MpmItemMdtoSumario>();
		Integer cont = 0;
		
		MpmPrescricaoMdto prescrMedicamento = mpmPrescricaoMdtoDAO.obterMedicamentoPeloId(seqAtendimento, seqPrescricaoMedicamento);
		
		if(prescrMedicamento != null && !prescrMedicamento.getIndSolucao()){//Solucao false
			List<MpmItemPrescricaoMdto> listaItensPrescMdto =  mpmItemPrescricaoMdtoDAO.listarItensPrescricaoMedicamentoPeloSeqEAtdSeq(seqAtendimento, seqPrescricaoMedicamento);
			for (MpmItemPrescricaoMdto itemPrescricaoMdto : listaItensPrescMdto) {
				temFilho = true;
				cont++;
				if(!primeiraVez){
					sintaxeMdto.append('\n');
				}else{
					primeiraVez = false;
				}
				sintaxeMdto.append(StringUtils.capitalize(StringUtils.lowerCase(itemPrescricaoMdto.getMedicamento().getDescricao())));//Conforme mapeamento, medicamento e descricao sao notNull.
				if(itemPrescricaoMdto.getMedicamento().getConcentracao() != null){
					sintaxeMdto.append(": ");
					sintaxeMdto.append(itemPrescricaoMdto.getMedicamento().getConcentracaoFormatada());
				}
				if(itemPrescricaoMdto.getMedicamento().getMpmUnidadeMedidaMedicas() != null){
					sintaxeMdto.append(' ');
					sintaxeMdto.append(StringUtils.lowerCase(itemPrescricaoMdto.getMedicamento().getMpmUnidadeMedidaMedicas().getDescricao()));//Conforme mapeamento, descricao e notnull
				}
				if(itemPrescricaoMdto.getObservacao() != null){
					sintaxeMdto.append(" : ");
					sintaxeMdto.append(StringUtils.lowerCase(itemPrescricaoMdto.getObservacao()));
				}
				sintaxeMdto.append(" - Administrar ");
				sintaxeMdto.append(itemPrescricaoMdto.getDoseFormatada());//Conforme mapeamento, dose eh notnull
				
				MpmItemMdtoSumario itemMdtoSumario = new MpmItemMdtoSumario();
				//tab_ims_sol(v_cont).ims_tfq_seq     				:= prcr_sol.tfq_seq;
				itemMdtoSumario.setTipoFreqAprazamento(prescrMedicamento.getTipoFreqAprazamento());
				//tab_ims_sol(v_cont).ims_tva_seq     				:= prcr_sol.tva_seq;
				itemMdtoSumario.setTipoVelocidadeAdministracao(prescrMedicamento.getTipoVelocAdministracao());
				//tab_ims_sol(v_cont).ims_med_mat_codigo_diluente	:= prcr_sol.med_mat_codigo;
				itemMdtoSumario.setDiluente(prescrMedicamento.getDiluente());
				//tab_ims_sol(v_cont).ims_mat_codigo  				:= prcr_item_sol.mat_codigo;
				itemMdtoSumario.setMedicamento(itemPrescricaoMdto.getMedicamento());
				//tab_ims_sol(v_cont).ims_fds_seq     				:= prcr_item_sol.fds_seq;
				itemMdtoSumario.setFormaDosagem(itemPrescricaoMdto.getFormaDosagem());
				//tab_ims_sol(v_cont).ims_dose        				:= prcr_item_sol.dose;
				itemMdtoSumario.setDose(itemPrescricaoMdto.getDose());
				
				sintaxeMdto.append(' ');
				if(itemPrescricaoMdto.getFormaDosagem().getUnidadeMedidaMedicas() != null){//Conforme mapeamento, FormaDosagem eh notnull.
					sintaxeMdto.append(StringUtils.lowerCase(itemPrescricaoMdto.getFormaDosagem().getUnidadeMedidaMedicas().getDescricao()));//Conforme mapeamento, descricao eh notnull
				}else{
					sintaxeMdto.append(StringUtils.lowerCase(itemPrescricaoMdto.getMedicamento().getTipoApresentacaoMedicamento() != null ? itemPrescricaoMdto.getMedicamento().getTipoApresentacaoMedicamento().getSigla() : ""));//Conforme mapeamento, medicamento eh notnull
				}
				
				if(itemPrescricaoMdto.getObservacao()!= null){
					itemMdtoSumario.setObservacaoItem(itemPrescricaoMdto.getObservacao());
				}
				//tab_ims_sol(v_cont).ims_frequencia := prcr_sol.frequencia;
				itemMdtoSumario.setFrequencia(prescrMedicamento.getFrequencia());
				
				//tab_ims_sol(v_cont).ims_hora_inicio_administracao := prcr_sol.hora_inicio_administracao;
				itemMdtoSumario.setHoraInicioAdministracao(prescrMedicamento.getHoraInicioAdministracao());
				//tab_ims_sol(v_cont).ims_qtde_horas_correr         := prcr_sol.qtde_horas_correr;
				itemMdtoSumario.setQtdeHorasCorrer(prescrMedicamento.getQtdeHorasCorrer() != null ? prescrMedicamento.getQtdeHorasCorrer().byteValue() : null);
				//tab_ims_sol(v_cont).ims_gotejo                    := prcr_sol.gotejo;
				itemMdtoSumario.setGotejo(prescrMedicamento.getGotejo());
				//tab_ims_sol(v_cont).ims_ind_se_necessario         := prcr_sol.ind_se_necessario;
				itemMdtoSumario.setIndSeNecessario(prescrMedicamento.getIndSeNecessario());
				//tab_ims_sol(v_cont).ims_ind_bomba_infusao         := prcr_sol.ind_bomba_infusao;
				itemMdtoSumario.setIndBombaInfusao(prescrMedicamento.getIndBombaInfusao());
				//tab_ims_sol(v_cont).ims_observacao                := prcr_sol.observacao;
				itemMdtoSumario.setObservacao(prescrMedicamento.getObservacao());
				//tab_ims_sol(v_cont).ims_vad_sigla                 := prcr_sol.vadmsigla;
				itemMdtoSumario.setViaAdministracao(prescrMedicamento.getViaAdministracao());
				//tab_ims_sol(v_cont).ims_unid_horas_correr         := prcr_sol.unid_horas_correr;
				if(DominioUnidadeHorasMinutos.H.equals(prescrMedicamento.getUnidHorasCorrer())){
					itemMdtoSumario.setUnidHorasCorrer(DominioUnidadeCorrer.H);
				}else if(DominioUnidadeHorasMinutos.M.equals(prescrMedicamento.getUnidHorasCorrer())){
					itemMdtoSumario.setUnidHorasCorrer(DominioUnidadeCorrer.M);
				}
				//tab_ims_sol(v_cont).ims_volume_diluente_ml        := prcr_sol.volume_diluente_ml;
				itemMdtoSumario.setVolumeDiluenteMl(prescrMedicamento.getVolumeDiluenteMl());
				
				itemMdtoSumarioMap.put(cont, itemMdtoSumario);
				
			}//fim do for
			if(temFilho){
				sintaxeMdto.append(", ");
				sintaxeMdto.append(prescrMedicamento.getViaAdministracao().getSigla());//Conforme mapeamento, ViaAdministracao e Sigla sao notNull.

				sintaxeMdto.append(", ");
				if(prescrMedicamento.getTipoFreqAprazamento().getSintaxe() != null){
					sintaxeMdto.append(StringUtils.replace(prescrMedicamento.getTipoFreqAprazamento().getSintaxe(), "#", prescrMedicamento.getFrequencia() != null ? prescrMedicamento.getFrequencia().toString() : ""));
				}else{
					sintaxeMdto.append(prescrMedicamento.getTipoFreqAprazamento().getDescricao());//Conforme mapeamento, TipoFreqAprazamento e Descricao sao notNull
				}
				
				if(prescrMedicamento.getHoraInicioAdministracao() != null){
					sintaxeMdto.append(", I=");
					SimpleDateFormat format = new SimpleDateFormat("HH:mm");  
					sintaxeMdto.append(format.format(prescrMedicamento.getHoraInicioAdministracao()));
					sintaxeMdto.append(" h ");
				}
				
				if(prescrMedicamento.getDiluente() != null && prescrMedicamento.getVolumeDiluenteMl() != null){
					sintaxeMdto.append(", Diluir em ");
					sintaxeMdto.append(prescrMedicamento.getVolumeDiluenteMl());
					sintaxeMdto.append(" ml de ");
					sintaxeMdto.append(prescrMedicamento.getDiluente().getDescricao());
				}

				if(prescrMedicamento.getDiluente() != null && prescrMedicamento.getVolumeDiluenteMl() == null){
					sintaxeMdto.append(", Diluir em ");
					sintaxeMdto.append(prescrMedicamento.getDiluente().getDescricao());
				}

				if(prescrMedicamento.getDiluente() == null && prescrMedicamento.getVolumeDiluenteMl() != null){
					sintaxeMdto.append(", Diluir em ");
					sintaxeMdto.append(prescrMedicamento.getVolumeDiluenteMl());
					sintaxeMdto.append(" ml");
				}
				
				if(prescrMedicamento.getQtdeHorasCorrer() != null){
					sintaxeMdto.append(", Correr em ");
					sintaxeMdto.append(prescrMedicamento.getQtdeHorasCorrer());
					if(prescrMedicamento.getUnidHorasCorrer() == null || DominioUnidadeHorasMinutos.H.equals(prescrMedicamento.getUnidHorasCorrer()) ){
						sintaxeMdto.append(" horas");
					}else{
						sintaxeMdto.append(" minutos");
					}
				}
				
				if(prescrMedicamento.getGotejo() != null){
					sintaxeMdto.append(", Gotejo ");
					sintaxeMdto.append(prescrMedicamento.getGotejo());
					sintaxeMdto.append(' ');
					sintaxeMdto.append(prescrMedicamento.getTipoVelocAdministracao() != null ? 
							StringUtils.capitalize(StringUtils.lowerCase(prescrMedicamento.getTipoVelocAdministracao().getDescricao())) : "");//Conforme mapeamento, descricao eh notNull.
				}
				if(Boolean.TRUE.equals(prescrMedicamento.getIndBombaInfusao())){
					sintaxeMdto.append(", BI");
				}
				if(Boolean.TRUE.equals(prescrMedicamento.getIndSeNecessario())){
					sintaxeMdto.append(", se necessário");
				}
				if(prescrMedicamento.getObservacao() != null){
					sintaxeMdto.append('\n');
					sintaxeMdto.append(". Obs.: ");
					sintaxeMdto.append(prescrMedicamento.getObservacao());
				}
			}
		}
		
		List<MpmItemPrescricaoSumario> listaIps = mpmItemPrescricaoSumarioDAO.listarItensPrescricaoSumario(seqAtendimento, seqAtendimentoPaciente, sintaxeMdto.toString(), DominioTipoItemPrescricaoSumario.POSITIVO_6);
		MpmItemPrescricaoSumario itemPrescSum = new MpmItemPrescricaoSumario();
		if((listaIps == null || listaIps.isEmpty())){
			if (StringUtils.isNotBlank(sintaxeMdto.toString())) {
				incluiuItu = true;
				itemPrescSum.setDescricao(sintaxeMdto.toString());
				itemPrescSum.setTipo(DominioTipoItemPrescricaoSumario.POSITIVO_6);
				itemPrescSum.setOrigemPrescricao(DominioOrigemPrescricao.I);
				
				AghAtendimentoPacientesId atendimentoPacienteId = new AghAtendimentoPacientesId();
				atendimentoPacienteId.setAtdSeq(seqAtendimento);
				atendimentoPacienteId.setSeq(seqAtendimentoPaciente);
				
				AghAtendimentoPacientes atendimentoPaciente = getAghuFacade().obterAghAtendimentoPacientesPorChavePrimaria(atendimentoPacienteId);
				
				itemPrescSum.setAtendimentoPaciente(atendimentoPaciente);
				mpmItemPrescricaoSumarioDAO.persistir(itemPrescSum);
				mpmItemPrescricaoSumarioDAO.flush();
	
				ituSeq = itemPrescSum.getSeq();
			}
		} else {
			ituSeq = listaIps.get(0).getSeq();
		}
		if(incluiuItu){
			for(int i = 1; i <= cont; i++){
				MpmItemMdtoSumario itemMdtoSumTemp = itemMdtoSumarioMap.remove(i);

				MpmItemMdtoSumario itemMdtoSum = new MpmItemMdtoSumario();
				MpmItemMdtoSumarioId id = new MpmItemMdtoSumarioId();
				id.setItuSeq(ituSeq);
				id.setSeqp(i);
				itemMdtoSum.setId(id);
				
				itemMdtoSum.setItemPrescricaoSumario(itemPrescSum);
				
				//tab_ims_sol(v_cont).ims_tfq_seq     				:= prcr_sol.tfq_seq;
				itemMdtoSum.setTipoFreqAprazamento(itemMdtoSumTemp.getTipoFreqAprazamento());
				//tab_ims_sol(v_cont).ims_tva_seq     				:= prcr_sol.tva_seq;
				itemMdtoSum.setTipoVelocidadeAdministracao(itemMdtoSumTemp.getTipoVelocidadeAdministracao());
				//tab_ims_sol(v_cont).ims_med_mat_codigo_diluente	:= prcr_sol.med_mat_codigo;
				itemMdtoSum.setDiluente(itemMdtoSumTemp.getDiluente());
				//tab_ims_sol(v_cont).ims_mat_codigo  				:= prcr_item_sol.mat_codigo;
				itemMdtoSum.setMedicamento(itemMdtoSumTemp.getMedicamento());
				//tab_ims_sol(v_cont).ims_fds_seq     				:= prcr_item_sol.fds_seq;
				itemMdtoSum.setFormaDosagem(itemMdtoSumTemp.getFormaDosagem());
				//tab_ims_sol(v_cont).ims_dose        				:= prcr_item_sol.dose;
				itemMdtoSum.setDose(itemMdtoSumTemp.getDose());

				if(StringUtils.isNotBlank(itemMdtoSumTemp.getObservacaoItem())){ //Corrigido e não feito merge na revision 18636
					itemMdtoSum.setObservacaoItem(itemMdtoSumTemp.getObservacaoItem());
				}
				//tab_ims_sol(v_cont).ims_frequencia := prcr_sol.frequencia;
				itemMdtoSum.setFrequencia(itemMdtoSumTemp.getFrequencia());
				//tab_ims_sol(v_cont).ims_hora_inicio_administracao := prcr_sol.hora_inicio_administracao;
				itemMdtoSum.setHoraInicioAdministracao(itemMdtoSumTemp.getHoraInicioAdministracao());
				//tab_ims_sol(v_cont).ims_qtde_horas_correr         := prcr_sol.qtde_horas_correr;
				itemMdtoSum.setQtdeHorasCorrer(itemMdtoSumTemp.getQtdeHorasCorrer());
				//tab_ims_sol(v_cont).ims_gotejo                    := prcr_sol.gotejo;
				itemMdtoSum.setGotejo(itemMdtoSumTemp.getGotejo());
				//tab_ims_sol(v_cont).ims_ind_se_necessario         := prcr_sol.ind_se_necessario;
				itemMdtoSum.setIndSeNecessario(itemMdtoSumTemp.getIndSeNecessario());
				//tab_ims_sol(v_cont).ims_ind_bomba_infusao         := prcr_sol.ind_bomba_infusao;
				itemMdtoSum.setIndBombaInfusao(itemMdtoSumTemp.getIndBombaInfusao());
				//tab_ims_sol(v_cont).ims_observacao                := prcr_sol.observacao;
				itemMdtoSum.setObservacao(itemMdtoSumTemp.getObservacao());
				//tab_ims_sol(v_cont).ims_vad_sigla                 := prcr_sol.vadmsigla;
				itemMdtoSum.setViaAdministracao(itemMdtoSumTemp.getViaAdministracao());
				//tab_ims_sol(v_cont).ims_unid_horas_correr         := prcr_sol.unid_horas_correr;
				itemMdtoSum.setUnidHorasCorrer(itemMdtoSumTemp.getUnidHorasCorrer());
				//tab_ims_sol(v_cont).ims_volume_diluente_ml        := prcr_sol.volume_diluente_ml;
				itemMdtoSum.setVolumeDiluenteMl(itemMdtoSumTemp.getVolumeDiluenteMl());
				
				itemMdtoSum.setIndSolucao(false);
				
				mpmItemMdtoSumarioDAO.persistir(itemMdtoSum);
				mpmItemMdtoSumarioDAO.flush();
			}
		}
		
		return ituSeq;
	}

	/**
	 * ORADB Procedure MPMK_SINTAXE_SUMARIO.MPMP_SINT_SUMR_SOL
	 * 
	 * Esta rotina monta a sintaxe do sumario do item das solucoes.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public Integer montaSintaxeSumarioItemSolucoes(Integer seqAtendimento,
			Integer seqAtendimentoPaciente, Long seqPrescricaoMedicamento)
			throws ApplicationBusinessException {
		MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO = getMpmPrescricaoMdtoDAO();
		MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtoDAO = getMpmItemPrescricaoMdtoDAO();
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmItemMdtoSumarioDAO mpmItemMdtoSumarioDAO = getMpmItemMdtoSumarioDAO();
		
		Boolean temFilho = false;
		Boolean primeiraVez = true;
		Boolean incluiuItu = false;
		Integer ituSeq = 0;
		StringBuilder sintaxeSol = new StringBuilder("");
		HashMap<Integer, MpmItemMdtoSumario> itemMdtoSumarioMap = new HashMap<Integer, MpmItemMdtoSumario>();
		Integer cont = 0;
		
		MpmPrescricaoMdto solucao = mpmPrescricaoMdtoDAO.obterMedicamentoPeloId(seqAtendimento, seqPrescricaoMedicamento);
		
		if(solucao != null && solucao.getIndSolucao()){
			List<MpmItemPrescricaoMdto> listaItensPrescSol =  mpmItemPrescricaoMdtoDAO.listarItensPrescricaoMedicamentoPeloSeqEAtdSeq(seqAtendimento, seqPrescricaoMedicamento);
			for (MpmItemPrescricaoMdto itemPrescricaoSol : listaItensPrescSol) {
				temFilho = true;
				cont++;
				if(!primeiraVez){
					sintaxeSol.append('\n');
				}else{
					primeiraVez = false;
				}
				sintaxeSol.append(StringUtils.capitalize(StringUtils.lowerCase(itemPrescricaoSol.getMedicamento().getDescricao())));//Conforme mapeamento, medicamento e descricao sao notNull.
				if(itemPrescricaoSol.getMedicamento().getConcentracao() != null){
					sintaxeSol.append(": ");
					sintaxeSol.append(itemPrescricaoSol.getMedicamento().getConcentracaoFormatada());
				}
				if(itemPrescricaoSol.getMedicamento().getMpmUnidadeMedidaMedicas() != null){
					sintaxeSol.append(' ');
					sintaxeSol.append(StringUtils.lowerCase(itemPrescricaoSol.getMedicamento().getMpmUnidadeMedidaMedicas().getDescricao()));//Conforme mapeamento, descricao e notnull
				}
				if(itemPrescricaoSol.getObservacao() != null){
					sintaxeSol.append(" : ");
					sintaxeSol.append(StringUtils.lowerCase(itemPrescricaoSol.getObservacao()));
				}
				sintaxeSol.append(" - Diluir ");
				sintaxeSol.append(StringUtils.capitalize(StringUtils.lowerCase(itemPrescricaoSol.getDoseFormatada())));//Conforme mapeamento, dose eh notnull
				
				MpmItemMdtoSumario itemMdtoSumario = new MpmItemMdtoSumario();
				//tab_ims_sol(v_cont).ims_tfq_seq     				:= prcr_sol.tfq_seq;
				itemMdtoSumario.setTipoFreqAprazamento(solucao.getTipoFreqAprazamento());
				//tab_ims_sol(v_cont).ims_tva_seq     				:= prcr_sol.tva_seq;
				itemMdtoSumario.setTipoVelocidadeAdministracao(solucao.getTipoVelocAdministracao());
				//tab_ims_sol(v_cont).ims_med_mat_codigo_diluente	:= prcr_sol.med_mat_codigo;
				itemMdtoSumario.setDiluente(solucao.getDiluente());
				//tab_ims_sol(v_cont).ims_mat_codigo  				:= prcr_item_sol.mat_codigo;
				itemMdtoSumario.setMedicamento(itemPrescricaoSol.getMedicamento());
				//tab_ims_sol(v_cont).ims_fds_seq     				:= prcr_item_sol.fds_seq;
				itemMdtoSumario.setFormaDosagem(itemPrescricaoSol.getFormaDosagem());
				//tab_ims_sol(v_cont).ims_dose        				:= prcr_item_sol.dose;
				itemMdtoSumario.setDose(itemPrescricaoSol.getDose());
				
				sintaxeSol.append(' ');
				if(itemPrescricaoSol.getFormaDosagem().getUnidadeMedidaMedicas() != null){//Conforme mapeamento, FormaDosagem eh notnull.
					sintaxeSol.append(StringUtils.lowerCase(itemPrescricaoSol.getFormaDosagem().getUnidadeMedidaMedicas().getDescricao()));//Conforme mapeamento, descricao eh notnull
				}else{
					sintaxeSol.append(StringUtils.lowerCase(itemPrescricaoSol.getMedicamento().getTipoApresentacaoMedicamento() != null ? itemPrescricaoSol.getMedicamento().getTipoApresentacaoMedicamento().getSigla() : ""));//Conforme mapeamento, medicamento eh notnull
				}
				
				if(itemPrescricaoSol.getObservacao()!= null){
					itemMdtoSumario.setObservacaoItem(itemPrescricaoSol.getObservacao());
				}
				//tab_ims_sol(v_cont).ims_frequencia := prcr_sol.frequencia;
				itemMdtoSumario.setFrequencia(solucao.getFrequencia());
				
				//tab_ims_sol(v_cont).ims_hora_inicio_administracao := prcr_sol.hora_inicio_administracao;
				itemMdtoSumario.setHoraInicioAdministracao(solucao.getHoraInicioAdministracao());
				//tab_ims_sol(v_cont).ims_qtde_horas_correr         := prcr_sol.qtde_horas_correr;
				itemMdtoSumario.setQtdeHorasCorrer(solucao.getQtdeHorasCorrer() != null ? solucao.getQtdeHorasCorrer().byteValue() : null);
				//tab_ims_sol(v_cont).ims_gotejo                    := prcr_sol.gotejo;
				itemMdtoSumario.setGotejo(solucao.getGotejo());
				//tab_ims_sol(v_cont).ims_ind_se_necessario         := prcr_sol.ind_se_necessario;
				itemMdtoSumario.setIndSeNecessario(solucao.getIndSeNecessario());
				//tab_ims_sol(v_cont).ims_ind_bomba_infusao         := prcr_sol.ind_bomba_infusao;
				itemMdtoSumario.setIndBombaInfusao(solucao.getIndBombaInfusao());
				//tab_ims_sol(v_cont).ims_observacao                := prcr_sol.observacao;
				itemMdtoSumario.setObservacao(solucao.getObservacao());
				//tab_ims_sol(v_cont).ims_vad_sigla                 := prcr_sol.vadmsigla;
				itemMdtoSumario.setViaAdministracao(solucao.getViaAdministracao());
				//tab_ims_sol(v_cont).ims_unid_horas_correr         := prcr_sol.unid_horas_correr;
				if(DominioUnidadeHorasMinutos.H.equals(solucao.getUnidHorasCorrer())){
					itemMdtoSumario.setUnidHorasCorrer(DominioUnidadeCorrer.H);
				}else if(DominioUnidadeHorasMinutos.M.equals(solucao.getUnidHorasCorrer())){
					itemMdtoSumario.setUnidHorasCorrer(DominioUnidadeCorrer.M);
				}
				//tab_ims_sol(v_cont).ims_volume_diluente_ml        := prcr_sol.volume_diluente_ml;
				itemMdtoSumario.setVolumeDiluenteMl(solucao.getVolumeDiluenteMl());
				
				itemMdtoSumarioMap.put(cont, itemMdtoSumario);
				
			}//fim do for
			if(temFilho){
				sintaxeSol.append('\n');
				sintaxeSol.append(solucao.getViaAdministracao().getSigla());//Conforme mapeamento, ViaAdministracao e Sigla sao notNull.

				sintaxeSol.append(", ");
				if(solucao.getTipoFreqAprazamento().getSintaxe() != null){
					sintaxeSol.append(StringUtils.replace(solucao.getTipoFreqAprazamento().getSintaxe(), "#", solucao.getFrequencia() != null ? solucao.getFrequencia().toString() : ""));
				}else{
					sintaxeSol.append(solucao.getTipoFreqAprazamento().getDescricao());//Conforme mapeamento, TipoFreqAprazamento e Descricao sao notNull
				}
				
				if(solucao.getHoraInicioAdministracao() != null){
					sintaxeSol.append(", I=");
					SimpleDateFormat format = new SimpleDateFormat("HH:mm");  
					sintaxeSol.append(format.format(solucao.getHoraInicioAdministracao()));
					sintaxeSol.append(" h ");
				}
				if(solucao.getQtdeHorasCorrer() != null){
					if(solucao.getHoraInicioAdministracao() != null){
						sintaxeSol.append(", ");
					}
					sintaxeSol.append("Correr em ");
					sintaxeSol.append(solucao.getQtdeHorasCorrer());
					if(solucao.getUnidHorasCorrer() == null || DominioUnidadeHorasMinutos.H.equals(solucao.getUnidHorasCorrer()) ){
						sintaxeSol.append(" horas");
					}else{
						sintaxeSol.append(" minutos");
					}
				}
				if(solucao.getGotejo() != null){
					sintaxeSol.append(", Gotejo ");
					sintaxeSol.append(solucao.getGotejo());
					sintaxeSol.append(' ');
					sintaxeSol.append(solucao.getTipoVelocAdministracao() != null ? solucao.getTipoVelocAdministracao().getDescricao() : "");//Conforme mapeamento, descricao eh notNull.
				}
				if(Boolean.TRUE.equals(solucao.getIndBombaInfusao())){
					sintaxeSol.append(", BI");
				}
				if(Boolean.TRUE.equals(solucao.getIndSeNecessario())){
					sintaxeSol.append(", se necessário");
				}
				if(solucao.getObservacao() != null){
					sintaxeSol.append('\n');
					sintaxeSol.append("Obs.: ");
					sintaxeSol.append(solucao.getObservacao());
				}
			}
		}
		List<MpmItemPrescricaoSumario> listaIps = mpmItemPrescricaoSumarioDAO.listarItensPrescricaoSumario(seqAtendimento, seqAtendimentoPaciente, sintaxeSol.toString(), DominioTipoItemPrescricaoSumario.POSITIVO_8);
		MpmItemPrescricaoSumario itemPrescSum = new MpmItemPrescricaoSumario();
		if((listaIps == null || listaIps.isEmpty())) {
			if (StringUtils.isNotBlank(sintaxeSol.toString())) {
				incluiuItu = true;
				itemPrescSum.setDescricao(sintaxeSol.toString());
				itemPrescSum.setTipo(DominioTipoItemPrescricaoSumario.POSITIVO_8);
				itemPrescSum.setOrigemPrescricao(DominioOrigemPrescricao.I);
				
				AghAtendimentoPacientesId atendimentoPacienteId = new AghAtendimentoPacientesId();
				atendimentoPacienteId.setAtdSeq(seqAtendimento);
				atendimentoPacienteId.setSeq(seqAtendimentoPaciente);
				
				AghAtendimentoPacientes atendimentoPaciente = getAghuFacade().obterAghAtendimentoPacientesPorChavePrimaria(atendimentoPacienteId);
				
				itemPrescSum.setAtendimentoPaciente(atendimentoPaciente);
				mpmItemPrescricaoSumarioDAO.persistir(itemPrescSum);
				mpmItemPrescricaoSumarioDAO.flush();
	
				ituSeq = itemPrescSum.getSeq(); 
			}
		} else {
			ituSeq = listaIps.get(0).getSeq();
		}
		if(incluiuItu){
			for(int i = 1; i <= cont; i++){
				MpmItemMdtoSumario itemMdtoSumTemp = itemMdtoSumarioMap.remove(i);

				MpmItemMdtoSumario itemMdtoSum = new MpmItemMdtoSumario();
				MpmItemMdtoSumarioId id = new MpmItemMdtoSumarioId();
				id.setItuSeq(ituSeq);
				id.setSeqp(i);
				itemMdtoSum.setId(id);

				itemMdtoSum.setItemPrescricaoSumario(itemPrescSum);
				
				//tab_ims_sol(v_cont).ims_tfq_seq     				:= prcr_sol.tfq_seq;
				itemMdtoSum.setTipoFreqAprazamento(itemMdtoSumTemp.getTipoFreqAprazamento());
				//tab_ims_sol(v_cont).ims_tva_seq     				:= prcr_sol.tva_seq;
				itemMdtoSum.setTipoVelocidadeAdministracao(itemMdtoSumTemp.getTipoVelocidadeAdministracao());
				//tab_ims_sol(v_cont).ims_med_mat_codigo_diluente	:= prcr_sol.med_mat_codigo;
				itemMdtoSum.setDiluente(itemMdtoSumTemp.getDiluente());
				//tab_ims_sol(v_cont).ims_mat_codigo  				:= prcr_item_sol.mat_codigo;
				itemMdtoSum.setMedicamento(itemMdtoSumTemp.getMedicamento());
				//tab_ims_sol(v_cont).ims_fds_seq     				:= prcr_item_sol.fds_seq;
				itemMdtoSum.setFormaDosagem(itemMdtoSumTemp.getFormaDosagem());
				//tab_ims_sol(v_cont).ims_dose        				:= prcr_item_sol.dose;
				itemMdtoSum.setDose(itemMdtoSumTemp.getDose());
				if(StringUtils.isNotBlank(itemMdtoSumTemp.getObservacaoItem())){ //Corrigido e nao feito merge na revision 18627
					itemMdtoSum.setObservacaoItem(itemMdtoSumTemp.getObservacaoItem());
				}
				//tab_ims_sol(v_cont).ims_frequencia := prcr_sol.frequencia;
				itemMdtoSum.setFrequencia(itemMdtoSumTemp.getFrequencia());
				//tab_ims_sol(v_cont).ims_hora_inicio_administracao := prcr_sol.hora_inicio_administracao;
				itemMdtoSum.setHoraInicioAdministracao(itemMdtoSumTemp.getHoraInicioAdministracao());
				//tab_ims_sol(v_cont).ims_qtde_horas_correr         := prcr_sol.qtde_horas_correr;
				itemMdtoSum.setQtdeHorasCorrer(itemMdtoSumTemp.getQtdeHorasCorrer());
				//tab_ims_sol(v_cont).ims_gotejo                    := prcr_sol.gotejo;
				itemMdtoSum.setGotejo(itemMdtoSumTemp.getGotejo());
				//tab_ims_sol(v_cont).ims_ind_se_necessario         := prcr_sol.ind_se_necessario;
				itemMdtoSum.setIndSeNecessario(itemMdtoSumTemp.getIndSeNecessario());
				//tab_ims_sol(v_cont).ims_ind_bomba_infusao         := prcr_sol.ind_bomba_infusao;
				itemMdtoSum.setIndBombaInfusao(itemMdtoSumTemp.getIndBombaInfusao());
				//tab_ims_sol(v_cont).ims_observacao                := prcr_sol.observacao;
				itemMdtoSum.setObservacao(itemMdtoSumTemp.getObservacao());
				//tab_ims_sol(v_cont).ims_vad_sigla                 := prcr_sol.vadmsigla;
				itemMdtoSum.setViaAdministracao(itemMdtoSumTemp.getViaAdministracao());
				//tab_ims_sol(v_cont).ims_unid_horas_correr         := prcr_sol.unid_horas_correr;
				itemMdtoSum.setUnidHorasCorrer(itemMdtoSumTemp.getUnidHorasCorrer());
				//tab_ims_sol(v_cont).ims_volume_diluente_ml        := prcr_sol.volume_diluente_ml;
				itemMdtoSum.setVolumeDiluenteMl(itemMdtoSumTemp.getVolumeDiluenteMl());
				
				itemMdtoSum.setIndSolucao(true);
				
				mpmItemMdtoSumarioDAO.persistir(itemMdtoSum);
				mpmItemMdtoSumarioDAO.flush();
			}
		}
		
		return ituSeq;
	}

	/**
	 * ORADB Procedure MPMK_SINTAXE_SUMARIO.MPMP_SINT_SUMR_PROC
	 * 
	 * Esta rotina monta a sintaxe do sumario do item dos procedimentos.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public Integer montaSintaxeSumarioItemProcedimento(Integer seqAtendimento,
			Integer seqAtendimentoPaciente, Long seqPrescricaoProcedimento)
			throws ApplicationBusinessException {
		MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO = getMpmPrescricaoProcedimentoDAO();
		MpmModoUsoPrescProcedDAO mpmModoUsoPrescProcedDAO = getMpmModoUsoPrescProcedDAO();
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmItemProcedimentoSumarioDAO mpmItemProcedimentoSumarioDAO = getMpmItemProcedimentoSumarioDAO();
		
		Boolean outrosFilhos = false;
		Boolean incluiuItu = false;
		Integer ituSeq = 0;
		StringBuilder sintaxeProc = new StringBuilder("");
		HashMap<Integer, MpmItemProcedimentoSumario> itemProcedimentoSumarioMap = new HashMap<Integer, MpmItemProcedimentoSumario>();
		Integer cont = 1;
		
		MpmPrescricaoProcedimento prescricaoProcedimento = mpmPrescricaoProcedimentoDAO.obterPrescricaoProcedimentoPorId(seqPrescricaoProcedimento, seqAtendimento);
		if(prescricaoProcedimento != null){
			sintaxeProc.append(StringUtils.capitalize(StringUtils.lowerCase(prescricaoProcedimento.getProcedimentoEspecialDiverso() != null ? prescricaoProcedimento.getProcedimentoEspecialDiverso().getDescricao() : "")));
			sintaxeProc.append(StringUtils.capitalize(StringUtils.lowerCase(prescricaoProcedimento.getProcedimentoCirurgico() != null ? prescricaoProcedimento.getProcedimentoCirurgico().getDescricao() : "")));
			sintaxeProc.append(StringUtils.capitalize(StringUtils.lowerCase(prescricaoProcedimento.getMatCodigo() != null ? prescricaoProcedimento.getMatCodigo().getNome(): "")));
			if(prescricaoProcedimento.getQuantidade() != null){
				sintaxeProc.append(' ');
				sintaxeProc.append(prescricaoProcedimento.getQuantidade());
				sintaxeProc.append(StringUtils.capitalize(StringUtils.lowerCase(prescricaoProcedimento.getMatCodigo() != null ? prescricaoProcedimento.getMatCodigo().getUmdCodigo() : "")));
			}
			
			MpmItemProcedimentoSumario itemProcedimentoSumario = new MpmItemProcedimentoSumario();
			//tab_ppr(v_cont).ppr_mat_codigo         := mod_proc.mat_codigo;
			itemProcedimentoSumario.setMaterial(prescricaoProcedimento.getMatCodigo());
			//tab_ppr(v_cont).ppr_pci_seq            := mod_proc.pci_seq;
			itemProcedimentoSumario.setProcedimentoCirurgico(prescricaoProcedimento.getProcedimentoCirurgico());
			//tab_ppr(v_cont).ppr_ped_seq            := mod_proc.ped_seq;
			itemProcedimentoSumario.setMpmProcedEspecialDiversos(prescricaoProcedimento.getProcedimentoEspecialDiverso());
			//tab_ppr(v_cont).ppr_inf_complementares := mod_proc.inf_complementares;
			itemProcedimentoSumario.setInfComplementares(prescricaoProcedimento.getInformacaoComplementar());
			//tab_ppr(v_cont).ppr_quantidade         := mod_proc.quantidade;
			itemProcedimentoSumario.setQuantidade(prescricaoProcedimento.getQuantidade());
			
			itemProcedimentoSumarioMap.put(cont, itemProcedimentoSumario);
			
			List<MpmModoUsoPrescProced> listaUsoPrescProced = mpmModoUsoPrescProcedDAO.obterPrescricaoProcedimentoPorAtendimentoESeq(seqPrescricaoProcedimento, seqAtendimento);
			for (MpmModoUsoPrescProced modoUsoPrescProced : listaUsoPrescProced) {
				//if(StringUtils.isNotBlank(modoUsoPrescProced.getTipoModUsoProcedimento().getDescricao()))//Esta linha nao foi migrada pois TipoModUsoProcedimento e Descricao, conforme o mapeamento, nao podem ser nulos.
				if((prescricaoProcedimento.getProcedimentoEspecialDiverso() != null && StringUtils.isNotBlank(prescricaoProcedimento.getProcedimentoEspecialDiverso().getDescricao()))
					|| (prescricaoProcedimento.getMatCodigo() != null && StringUtils.isNotBlank(prescricaoProcedimento.getMatCodigo().getNome()))
					|| (prescricaoProcedimento.getMatCodigo() != null && StringUtils.isNotBlank(prescricaoProcedimento.getMatCodigo().getUmdCodigo()))
					|| (prescricaoProcedimento.getQuantidade()!= null))
				{
					sintaxeProc.append(", ");
				}
				sintaxeProc.append('\n');
				if(outrosFilhos){
					cont++;
					itemProcedimentoSumario = new MpmItemProcedimentoSumario();
					//tab_ppr(v_cont).ppr_mat_codigo         := mod_proc.mat_codigo;
					itemProcedimentoSumario.setMaterial(prescricaoProcedimento.getMatCodigo());
					//tab_ppr(v_cont).ppr_pci_seq            := mod_proc.pci_seq;
					itemProcedimentoSumario.setProcedimentoCirurgico(prescricaoProcedimento.getProcedimentoCirurgico());
					//tab_ppr(v_cont).ppr_ped_seq            := mod_proc.ped_seq;
					itemProcedimentoSumario.setMpmProcedEspecialDiversos(prescricaoProcedimento.getProcedimentoEspecialDiverso());
					//tab_ppr(v_cont).ppr_inf_complementares := mod_proc.inf_complementares;
					itemProcedimentoSumario.setInfComplementares(prescricaoProcedimento.getInformacaoComplementar());
					//tab_ppr(v_cont).ppr_quantidade         := mod_proc.quantidade;
					itemProcedimentoSumario.setQuantidade(prescricaoProcedimento.getQuantidade());
				}
				outrosFilhos = true;
				//tab_ppr(v_cont).ppr_tup_ped_seq        := modo_uso_proc.tup_ped_seq;
				//tab_ppr(v_cont).ppr_tup_seqp           := modo_uso_proc.tup_seqp;
				itemProcedimentoSumario.setMpmTipoModUsoProcedimentos(modoUsoPrescProced.getTipoModUsoProcedimento());

				sintaxeProc.append(StringUtils.capitalize(StringUtils.lowerCase(modoUsoPrescProced.getTipoModUsoProcedimento().getDescricao())));//TipoModUsoProcedimento e Descricao, conforme o mapeamento, nao podem ser nulos.
				
				if(modoUsoPrescProced.getQuantidade()!= null){
					sintaxeProc.append(' ');
					sintaxeProc.append(modoUsoPrescProced.getQuantidade());
					sintaxeProc.append(' ');
					if(modoUsoPrescProced.getTipoModUsoProcedimento().getUnidadeMedidaMedica() != null){//Conforme mapeamento, TipoModUsoProcedimento eh not null.
						sintaxeProc.append(StringUtils.capitalize(StringUtils.lowerCase(modoUsoPrescProced.getTipoModUsoProcedimento().getUnidadeMedidaMedica().getDescricao())));
					}
					//tab_ppr(v_cont).ppr_quantidade_modo_uso :=  modo_uso_proc.quantidade;
					itemProcedimentoSumario.setQuantidadeModoUso(modoUsoPrescProced.getQuantidade());
				}
				itemProcedimentoSumarioMap.put(cont, itemProcedimentoSumario);
				
			}//fim do for

			if(prescricaoProcedimento.getInformacaoComplementar() != null){
				sintaxeProc.append(". Inf. Complementares: ");
				sintaxeProc.append(prescricaoProcedimento.getInformacaoComplementar());
				
			}
		}
		List<MpmItemPrescricaoSumario> listaIps = mpmItemPrescricaoSumarioDAO.listarItensPrescricaoSumario(seqAtendimento, seqAtendimentoPaciente, sintaxeProc.toString(), DominioTipoItemPrescricaoSumario.POSITIVO_16);
		MpmItemPrescricaoSumario itemPrescSum = new MpmItemPrescricaoSumario();
		if((listaIps == null || listaIps.isEmpty())){
			if (StringUtils.isNotBlank(sintaxeProc.toString())) {
				incluiuItu = true;
				itemPrescSum.setDescricao(sintaxeProc.toString());
				itemPrescSum.setTipo(DominioTipoItemPrescricaoSumario.POSITIVO_16);
				itemPrescSum.setOrigemPrescricao(DominioOrigemPrescricao.I);
				
				AghAtendimentoPacientesId atendimentoPacienteId = new AghAtendimentoPacientesId();
				atendimentoPacienteId.setAtdSeq(seqAtendimento);
				atendimentoPacienteId.setSeq(seqAtendimentoPaciente);
				
				AghAtendimentoPacientes atendimentoPaciente = getAghuFacade().obterAghAtendimentoPacientesPorChavePrimaria(atendimentoPacienteId);
				
				itemPrescSum.setAtendimentoPaciente(atendimentoPaciente);
				mpmItemPrescricaoSumarioDAO.persistir(itemPrescSum);
				mpmItemPrescricaoSumarioDAO.flush();
	
				ituSeq = itemPrescSum.getSeq(); 
			}
		} else {
			ituSeq = listaIps.get(0).getSeq();
		}
		if(incluiuItu){
			for(int i = 1; i <= cont; i++){
				MpmItemProcedimentoSumario itemProcSumTemp = itemProcedimentoSumarioMap.remove(i);

				MpmItemProcedimentoSumario itemProcSum = new MpmItemProcedimentoSumario();
				MpmItemProcedimentoSumarioId id = new MpmItemProcedimentoSumarioId();
				id.setItuSeq(ituSeq);
				id.setSeqp(i);
				itemProcSum.setId(id);
				itemProcSum.setMpmItemPrescricaoSumarios(itemPrescSum);
				
				itemProcSum.setMaterial(itemProcSumTemp.getMaterial());
				itemProcSum.setProcedimentoCirurgico(itemProcSumTemp.getProcedimentoCirurgico());
				itemProcSum.setMpmProcedEspecialDiversos(itemProcSumTemp.getMpmProcedEspecialDiversos());
				itemProcSum.setMpmTipoModUsoProcedimentos(itemProcSumTemp.getMpmTipoModUsoProcedimentos());
				itemProcSum.setQuantidade(itemProcSumTemp.getQuantidade());
				itemProcSum.setInfComplementares(itemProcSumTemp.getInfComplementares());
				itemProcSum.setQuantidadeModoUso(itemProcSumTemp.getQuantidadeModoUso());

				mpmItemProcedimentoSumarioDAO.persistir(itemProcSum);
				mpmItemProcedimentoSumarioDAO.flush();
			}
		}
		return ituSeq;
	}

	/**
	 * ORADB Procedure MPMK_SINTAXE_SUMARIO.MPMP_SINT_SUMR_HEMO
	 * 
	 * Esta rotina monta a sintaxe do sumario do item da hemoterapia.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public Integer montaSintaxeSumarioItemHemoterapia(Integer seqAtendimento,
			Integer seqAtendimentoPaciente, Integer seqSolicitacaoHemoterapica)
			throws ApplicationBusinessException {
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmItemHemoterapiaSumarioDAO mpmItemHemoterapiaSumarioDAO = getMpmItemHemoterapiaSumarioDAO();

		Integer cont = 0;
		Boolean primeiraVez = true;
		Boolean incluiuItu = false;
		Integer ituSeq = 0;
		StringBuilder sintaxeHemo = new StringBuilder("");
		HashMap<Integer, MpmItemHemoterapiaSumario> itemHemoterapiaSumarioMap = new HashMap<Integer, MpmItemHemoterapiaSumario>();
		
		AbsSolicitacoesHemoterapicas solHem = getBancoDeSangueFacade().obterSolicitacoesHemoterapicas(seqAtendimento, seqSolicitacaoHemoterapica);
		if(solHem != null){
			List<AbsItensSolHemoterapicas> listaItensSolHemoterapicas = getBancoDeSangueFacade().buscaItensSolHemoterapicas(seqAtendimento, seqSolicitacaoHemoterapica);
			for (AbsItensSolHemoterapicas itemSolHemoterapicas : listaItensSolHemoterapicas) {
				cont++;
				MpmItemHemoterapiaSumario itemHemoSum = new MpmItemHemoterapiaSumario(); 

				if(itemSolHemoterapicas.getComponenteSanguineo() != null){
					if(!primeiraVez){
						sintaxeHemo.append('\n');
					}else{
						primeiraVez = false;
					}
					sintaxeHemo.append(StringUtils.capitalize(StringUtils.lowerCase(itemSolHemoterapicas.getComponenteSanguineo().getDescricao())));
				}else{ 
					if(!primeiraVez){
						sintaxeHemo.append('\n');
					}else{
						primeiraVez = false;
					}
					sintaxeHemo.append(StringUtils.capitalize(StringUtils.lowerCase(itemSolHemoterapicas.getProcedHemoterapico() != null ? itemSolHemoterapicas.getProcedHemoterapico().getDescricao() : "")));
				}
				if(itemSolHemoterapicas.getQtdeUnidades() != null){
					sintaxeHemo.append(' ');
					sintaxeHemo.append(itemSolHemoterapicas.getQtdeUnidades());
					if(itemSolHemoterapicas.getQtdeUnidades().byteValue() == 1){
						sintaxeHemo.append(" unidade");
					}else{
						sintaxeHemo.append(" unidades");
					}
					
					itemHemoSum.setQtdeUnidades(itemSolHemoterapicas.getQtdeUnidades());
					
				}
				if(itemSolHemoterapicas.getQtdeMl() != null){
					itemHemoSum.setQtdeMl(itemSolHemoterapicas.getQtdeMl());
					sintaxeHemo.append(' ');
					sintaxeHemo.append(itemSolHemoterapicas.getQtdeMl());
					sintaxeHemo.append("ml");
				}
				if(itemSolHemoterapicas.getTipoFreqAprazamento() != null && itemSolHemoterapicas.getTipoFreqAprazamento().getSintaxe() != null){
					if(StringUtils.isNotBlank(sintaxeHemo.toString())){
						sintaxeHemo.append(',');
					}
					sintaxeHemo.append(' ');
					sintaxeHemo.append(StringUtils.replace(itemSolHemoterapicas.getTipoFreqAprazamento().getSintaxe(), "#", itemSolHemoterapicas.getFrequencia() != null ? itemSolHemoterapicas.getFrequencia().toString() : ""));
				}else if(itemSolHemoterapicas.getTipoFreqAprazamento() != null){
					if(StringUtils.isNotBlank(sintaxeHemo.toString())){
						sintaxeHemo.append(',');
					}
					sintaxeHemo.append(' ');
					sintaxeHemo.append(itemSolHemoterapicas.getTipoFreqAprazamento().getDescricao());
				}
				
				if(itemSolHemoterapicas.getQtdeAplicacoes() != null){
					itemHemoSum.setQtdeAplicacoes(itemSolHemoterapicas.getQtdeAplicacoes());
					sintaxeHemo.append(", ");
					sintaxeHemo.append(itemSolHemoterapicas.getQtdeAplicacoes());
					if(itemSolHemoterapicas.getQtdeAplicacoes().byteValue() == 1){
						sintaxeHemo.append(" aplicação");
					}else{
						sintaxeHemo.append(" aplicações");
					}
				}
				
				itemHemoSum.setTipoFreqAprazamento(itemSolHemoterapicas.getTipoFreqAprazamento());
				itemHemoSum.setComponenteSanguineo(itemSolHemoterapicas.getComponenteSanguineo());
				itemHemoSum.setProcedimentoHemoterapico(itemSolHemoterapicas.getProcedHemoterapico());
				itemHemoSum.setIndIrradiado(itemSolHemoterapicas.getIndIrradiado());
				itemHemoSum.setIndFiltrado(itemSolHemoterapicas.getIndFiltrado());
				itemHemoSum.setIndLavado(itemSolHemoterapicas.getIndLavado());
				itemHemoSum.setIndAferese(itemSolHemoterapicas.getIndAferese());
				itemHemoSum.setFrequencia(itemSolHemoterapicas.getFrequencia());
				
				itemHemoSum.setObservacao(solHem.getObservacao());
				
				
				itemHemoterapiaSumarioMap.put(cont, itemHemoSum);
			}//fim do for
			
			if(solHem.getObservacao() != null){
				sintaxeHemo.append(", \n");
				sintaxeHemo.append("Obs.: ");
				sintaxeHemo.append(solHem.getObservacao());
			}
		}
		List<MpmItemPrescricaoSumario> listaIps = mpmItemPrescricaoSumarioDAO.listarItensPrescricaoSumario(seqAtendimento, seqAtendimentoPaciente, sintaxeHemo.toString(), DominioTipoItemPrescricaoSumario.POSITIVO_12);
		MpmItemPrescricaoSumario itemPrescSum = new MpmItemPrescricaoSumario();
		if((listaIps == null || listaIps.isEmpty())){
			if (StringUtils.isNotBlank(sintaxeHemo.toString())) {
				incluiuItu = true;
				itemPrescSum.setDescricao(sintaxeHemo.toString());
				itemPrescSum.setTipo(DominioTipoItemPrescricaoSumario.POSITIVO_12);
				itemPrescSum.setOrigemPrescricao(DominioOrigemPrescricao.I);
				
				AghAtendimentoPacientesId atendimentoPacienteId = new AghAtendimentoPacientesId();
				atendimentoPacienteId.setAtdSeq(seqAtendimento);
				atendimentoPacienteId.setSeq(seqAtendimentoPaciente);
				
				AghAtendimentoPacientes atendimentoPaciente = getAghuFacade().obterAghAtendimentoPacientesPorChavePrimaria(atendimentoPacienteId);
				
				itemPrescSum.setAtendimentoPaciente(atendimentoPaciente);
				mpmItemPrescricaoSumarioDAO.persistir(itemPrescSum);
				mpmItemPrescricaoSumarioDAO.flush();	
	
				ituSeq = itemPrescSum.getSeq(); 
			}
		} else {
			ituSeq = listaIps.get(0).getSeq();
		}
		if(incluiuItu){
			for(int i = 1; i <= cont; i++){
				MpmItemHemoterapiaSumario itemHemSumTemp = itemHemoterapiaSumarioMap.remove(i);

				MpmItemHemoterapiaSumario itemHemSum = new MpmItemHemoterapiaSumario();
				MpmItemHemoterapiaSumarioId id = new MpmItemHemoterapiaSumarioId();
				id.setItuSeq(ituSeq);
				id.setSeqp(i);
				itemHemSum.setId(id);
				itemHemSum.setItemPrescricaoSumario(itemPrescSum);
				
				itemHemSum.setTipoFreqAprazamento(itemHemSumTemp.getTipoFreqAprazamento());
				itemHemSum.setComponenteSanguineo(itemHemSumTemp.getComponenteSanguineo());
				itemHemSum.setProcedimentoHemoterapico(itemHemSumTemp.getProcedimentoHemoterapico());
				itemHemSum.setIndIrradiado(itemHemSumTemp.getIndIrradiado());
				itemHemSum.setIndFiltrado(itemHemSumTemp.getIndFiltrado());
				itemHemSum.setIndLavado(itemHemSumTemp.getIndLavado());
				itemHemSum.setIndAferese(itemHemSumTemp.getIndAferese());
				itemHemSum.setFrequencia(itemHemSumTemp.getFrequencia());
				itemHemSum.setQtdeAplicacoes(itemHemSumTemp.getQtdeAplicacoes());
				itemHemSum.setQtdeUnidades(itemHemSumTemp.getQtdeUnidades());
				itemHemSum.setQtdeMl(itemHemSumTemp.getQtdeMl());
				itemHemSum.setObservacao(itemHemSumTemp.getObservacao());
								
				mpmItemHemoterapiaSumarioDAO.persistir(itemHemSum);
				mpmItemHemoterapiaSumarioDAO.flush();
			}
		}
		return ituSeq;
	}

	/** 
	 * ORADB Procedure MPMK_SINTAXE_SUMARIO.MPMP_SINT_SUMR_CONS
	 * 
	 * Esta rotina monta a sintaxe do sumario do item da consultoria.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Integer montaSintaxeSumarioItemConsultoria(Integer seqAtendimento,
			Integer seqAtendimentoPaciente, Integer seqSolicitacaoConsultoria)
			throws ApplicationBusinessException {
		MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO = getMpmSolicitacaoConsultoriaDAO();
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmItemConsultoriaSumarioDAO mpmItemConsultoriaSumarioDAO = getMpmItemConsultoriaSumarioDAO();
		
		StringBuilder sintaxeCons = new StringBuilder("");
		Integer ituSeq = 0;
		String sintaxeConsAux = "";
		MpmSolicitacaoConsultoriaId solConsId = new MpmSolicitacaoConsultoriaId();
		solConsId.setAtdSeq(seqAtendimento);
		solConsId.setSeq(seqSolicitacaoConsultoria);
		MpmSolicitacaoConsultoria solConsul = mpmSolicitacaoConsultoriaDAO.obterPorChavePrimaria(solConsId);
		
		if(solConsul != null){
			if(DominioTipoSolicitacaoConsultoria.C.equals(solConsul.getTipo())){
				sintaxeCons.append("Consultoria ");
			}else{
				sintaxeCons.append("Avaliação pré-cirúrgica ");
			}
			if(DominioSimNao.S.equals(solConsul.getIndUrgencia())){
				sintaxeCons.append("URGENTE em ");
			}else{
				sintaxeCons.append("em ");
			}
			sintaxeCons.append(StringUtils.capitalize(StringUtils.lowerCase(solConsul.getEspecialidade().getNomeEspecialidade())));//Especialidade e NomeEspecialidade, conforme mapeamento, sao notNull
			sintaxeConsAux = StringUtils.stripEnd(sintaxeCons.toString(), " ");//Retira os espacos em branco somente ao final da string.
		}
		
		List<MpmItemPrescricaoSumario> listaIps = mpmItemPrescricaoSumarioDAO.listarItensPrescricaoSumario(seqAtendimento, seqAtendimentoPaciente, sintaxeConsAux, DominioTipoItemPrescricaoSumario.POSITIVO_10);
		MpmItemPrescricaoSumario itemPrescSum = new MpmItemPrescricaoSumario();
		Boolean incluiuItu = false;
		if((listaIps == null || listaIps.isEmpty())){
			incluiuItu = true;
			itemPrescSum.setDescricao(sintaxeConsAux);
			itemPrescSum.setTipo(DominioTipoItemPrescricaoSumario.POSITIVO_10);
			itemPrescSum.setOrigemPrescricao(DominioOrigemPrescricao.I);
			
			AghAtendimentoPacientesId atendimentoPacienteId = new AghAtendimentoPacientesId();
			atendimentoPacienteId.setAtdSeq(seqAtendimento);
			atendimentoPacienteId.setSeq(seqAtendimentoPaciente);
			
			AghAtendimentoPacientes atendimentoPaciente = getAghuFacade().obterAghAtendimentoPacientesPorChavePrimaria(atendimentoPacienteId);
			
			itemPrescSum.setAtendimentoPaciente(atendimentoPaciente);
			mpmItemPrescricaoSumarioDAO.persistir(itemPrescSum);
			mpmItemPrescricaoSumarioDAO.flush();

			ituSeq = itemPrescSum.getSeq(); 
		} else {
			ituSeq = listaIps.get(0).getSeq();
		}
		
		if(incluiuItu && solConsul != null){
			MpmItemConsultoriaSumario ics = new MpmItemConsultoriaSumario();

			MpmItemConsultoriaSumarioId id = new MpmItemConsultoriaSumarioId();
			id.setItuSeq(ituSeq);
			id.setSeqp(1);//Como nao esta em um loop, o seq sera sempre 1, conforme a procedure.
			ics.setId(id);
			
			ics.setItemPrescricaoSumario(itemPrescSum);
			
			if(DominioTipoSolicitacaoConsultoria.C.equals(solConsul.getTipo())){//Tipo, nos dois pojos, conforme o mapeamento, é notNull
				ics.setTipo(DominioItemContaHospitalar.C);
			}else{
				ics.setTipo(DominioItemContaHospitalar.A);
			}
			
			ics.setEspecialidade(solConsul.getEspecialidade());
			
			if(DominioSimNao.S.equals(solConsul.getIndUrgencia())){//IndUrgencia, conforme mapeamento, eh notNull
				ics.setIndUrgencia(true);
			}else{
				ics.setIndUrgencia(false);
			}
			mpmItemConsultoriaSumarioDAO.persistir(ics);
			mpmItemConsultoriaSumarioDAO.flush();
		}
		
		return ituSeq;

	}

	/**
	 * ORADB Procedure MPMK_SINTAXE_SUMARIO.MPMP_SINT_SUMR_NPT
	 * 
	 * Esta rotina monta a sintaxe do sumario do item da nutricao parenteral
	 * total.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public Integer montaSintaxeSumarioItemNutricaoParental(
			Integer seqAtendimento, Integer seqAtendimentoPaciente,
			Integer seqNutricaoParental) throws ApplicationBusinessException {
		MpmPrescricaoNptDAO mpmPrescricaoNptDAO = getMpmPrescricaoNptDAO();
		MpmComposicaoPrescricaoNptDAO mpmComposicaoPrescricaoNptDAO = getMpmComposicaoPrescricaoNptDAO();
		MpmItemPrescricaoNptDAO mpmItemPrescricaoNptDAO = getMpmItemPrescricaoNptDAO();
		MpmItemPrescricaoSumarioDAO mpmItemPrescricaoSumarioDAO = getMpmItemPrescricaoSumarioDAO();
		MpmItemNptSumarioDAO mpmItemNptSumarioDAO = getMpmItemNptSumarioDAO();
		

		Boolean primeiraVez = true;
		Boolean umaVez = true;
		Integer cont = 0;
		Integer ituSeq = 0;
		StringBuilder sintaxePnp = new StringBuilder(30);
		HashMap<Integer, MpmItemNptSumario> itemNptSumarioMap = new HashMap<Integer, MpmItemNptSumario>();
		
		MpmPrescricaoNpt prescNpt = mpmPrescricaoNptDAO.obterNutricaoParentalTotalPeloId(seqAtendimento, seqNutricaoParental);
		List<MpmComposicaoPrescricaoNpt> listaComposicaoPrescNpt = mpmComposicaoPrescricaoNptDAO.listarComposicoesPrescricaoNpt(seqAtendimento, prescNpt.getId().getSeq().longValue());
		for (MpmComposicaoPrescricaoNpt compPrescNpt : listaComposicaoPrescNpt) {
			umaVez = true;
			List<MpmItemPrescricaoNpt> itemPrescricaoNpt = mpmItemPrescricaoNptDAO.listarItensPrescricaoNpt(seqAtendimento, prescNpt.getId().getSeq(), compPrescNpt.getId().getSeqp());
			for (MpmItemPrescricaoNpt itemPrescNpt : itemPrescricaoNpt) {
				cont = cont + 1;
				
				MpmItemNptSumario ins = new MpmItemNptSumario(); 
				//tab_pnp(v_cont).pnp_observacao 				:= r_prcr_pnp.observacao;
				ins.setObservacao(prescNpt.getObservacao());
				//tab_pnp(v_cont).pnp_ind_segue_gotejo_padrao 	:= r_prcr_pnp.ind_segue_gotejo_padrao;
				ins.setSegueGotejoPadrao(prescNpt.getSegueGotejoPadrao());
				//tab_pnp(v_cont).pnp_ind_bomba_infusao	 		:= r_prcr_pnp.ind_bomba_infusao;
				ins.setBombaInfusao(prescNpt.getBombaInfusao());
				//tab_pnp(v_cont).pnp_ped_seq 					:= r_prcr_pnp.ped_seq;
				ins.setProcedimentoEspecialDiverso(prescNpt.getProcedEspecialDiversos());
				//tab_pnp(v_cont).pnp_fnp_seq 					:= r_prcr_pnp.fnp_seq;
				ins.setAfaFormulaNptPadrao(prescNpt.getAfaFormulaNptPadrao());//TODO Alterar quando o objeto for criado.
				//tab_pnp(v_cont).pnp_velocidade_administracao 	:= r_compos_prcr_pnp.velocidade_administracao;
				ins.setVelocidadeAdministracao(compPrescNpt.getVelocidadeAdministracao());
				//tab_pnp(v_cont).pnp_tva_seq 					:= r_compos_prcr_pnp.tva_seq;
				ins.setTipoVelocidadeAdministracao(compPrescNpt.getAfaTipoVelocAdministracoes());
				//tab_pnp(v_cont).pnp_qtde_horas_correr 			:= r_compos_prcr_pnp.qtde_horas_correr;
				ins.setQtdeHorasCorrer(compPrescNpt.getQtdeHorasCorrer());
				//tab_pnp(v_cont).pnp_tic_seq 					:= r_compos_prcr_pnp.tic_seq;
				ins.setTipoComposicao(compPrescNpt.getAfaTipoComposicoes());
				//tab_pnp(v_cont).pnp_qtde_prescrita 			:= r_item_prcr_pnp.qtde_prescrita;
				ins.setQtdePrescrita(itemPrescNpt.getQtdePrescrita());
				//tab_pnp(v_cont).pnp_fds_seq 					:= r_item_prcr_pnp.fds_seq;
				ins.setFormaDosagem(itemPrescNpt.getAfaFormaDosagens());
				//tab_pnp(v_cont).pnp_cnp_seq 					:= r_item_prcr_pnp.cnp_med_mat_codigo;
				ins.setComponenteNutricaoParental(itemPrescNpt.getAfaComponenteNpts());
				
				 
				itemNptSumarioMap.put(cont, ins);
				
				if(!primeiraVez){
					sintaxePnp.append('\n');
				}
				primeiraVez = false;

				sintaxePnp.append(' ');
				sintaxePnp.append(StringUtils.capitalize(StringUtils.lowerCase(itemPrescNpt.getAfaComponenteNpts().getDescricao())));//ComponenteNpts e Descricao, conforme mapeamento, sao notNull
				
				if(itemPrescNpt.getAfaComponenteNpts().getIndImpDoseSumario()){//ComponenteNpts e IndImpDoseSumario, conforme mapeamento, sao notNull
					sintaxePnp.append(' ');
					sintaxePnp.append(itemPrescNpt.getQtdePrescrita());
					if(itemPrescNpt.getUnidadeMedidaMedicas() !=  null && itemPrescNpt.getUnidadeMedidaMedicas().getDescricao() != null){
						sintaxePnp.append(' ');
						sintaxePnp.append(itemPrescNpt.getUnidadeMedidaMedicas().getDescricao());
					}else{
						sintaxePnp.append(' ');
						sintaxePnp.append(itemPrescNpt.getAfaComponenteNpts().getAfaMedicamentos().getTipoApresentacaoMedicamento().getSigla());//AfaComponenteNpts e AfaMedicamentos, conforme mapeamento, sao notNull
					}
				} 
				if(umaVez){
					umaVez = false;
					sintaxePnp.append(' ');
					sintaxePnp.append(compPrescNpt.getVelocidadeAdministracao());
					sintaxePnp.append(' ');
					sintaxePnp.append(compPrescNpt.getAfaTipoVelocAdministracoes() != null ? compPrescNpt.getAfaTipoVelocAdministracoes().getDescricao() : "");
				}
			}//fim do for interno
		}//fim do for externo
		
		sintaxePnp.append('\n');
		if(prescNpt.getSegueGotejoPadrao()){
			sintaxePnp.append("Segue gotejo padrão");
		}else{
			sintaxePnp.append("Não segue gotejo padrão");
		}
		if(prescNpt.getBombaInfusao()){
			sintaxePnp.append('\n');
			sintaxePnp.append("BI");
		}
		if(prescNpt.getObservacao() != null){
			sintaxePnp.append('\n');
			sintaxePnp.append(" Obs.: ");
			sintaxePnp.append(prescNpt.getObservacao());
		}
		
		List<MpmItemPrescricaoSumario> listaIps = mpmItemPrescricaoSumarioDAO.listarItensPrescricaoSumario(seqAtendimento, seqAtendimentoPaciente, sintaxePnp.toString(), DominioTipoItemPrescricaoSumario.POSITIVO_14);
		MpmItemPrescricaoSumario itemPrescSum = new MpmItemPrescricaoSumario();
		Boolean incluiuItu = false;
		if((listaIps == null || listaIps.isEmpty())){
			if (StringUtils.isNotBlank(sintaxePnp.toString())) {
				incluiuItu = true;
				itemPrescSum.setDescricao(sintaxePnp.toString());
				itemPrescSum.setTipo(DominioTipoItemPrescricaoSumario.POSITIVO_14);
				itemPrescSum.setOrigemPrescricao(DominioOrigemPrescricao.I);
				
				AghAtendimentoPacientesId atendimentoPacienteId = new AghAtendimentoPacientesId();
				atendimentoPacienteId.setAtdSeq(seqAtendimento);
				atendimentoPacienteId.setSeq(seqAtendimentoPaciente);
				
				AghAtendimentoPacientes atendimentoPaciente = getAghuFacade().obterAghAtendimentoPacientesPorChavePrimaria(atendimentoPacienteId);
				
				itemPrescSum.setAtendimentoPaciente(atendimentoPaciente);
				mpmItemPrescricaoSumarioDAO.persistir(itemPrescSum);
				mpmItemPrescricaoSumarioDAO.flush();
	
				ituSeq = itemPrescSum.getSeq(); 
			}
		} else {
			ituSeq = listaIps.get(0).getSeq();
		}
		
		if(incluiuItu){
			for(int i = 1; i <= cont; i++){
				
				MpmItemNptSumario itemNptSumarioTemp  = itemNptSumarioMap.remove(i);   
				
				MpmItemNptSumario ins = new MpmItemNptSumario();
				MpmItemNptSumarioId id = new MpmItemNptSumarioId();
				id.setItuSeq(ituSeq);
				id.setSeqp(i);
				ins.setId(id);
				ins.setItemPrescricaoSumario(itemPrescSum);

				ins.setObservacao(itemNptSumarioTemp.getObservacao());
				ins.setSegueGotejoPadrao(itemNptSumarioTemp.getSegueGotejoPadrao());
				ins.setBombaInfusao(itemNptSumarioTemp.getBombaInfusao());
				ins.setProcedimentoEspecialDiverso(itemNptSumarioTemp.getProcedimentoEspecialDiverso());
				ins.setAfaFormulaNptPadrao(itemNptSumarioTemp.getAfaFormulaNptPadrao());//TODO Alterar quando o objeto for criado.
				ins.setVelocidadeAdministracao(itemNptSumarioTemp.getVelocidadeAdministracao());
				ins.setTipoVelocidadeAdministracao(itemNptSumarioTemp.getTipoVelocidadeAdministracao());
				ins.setQtdeHorasCorrer(itemNptSumarioTemp.getQtdeHorasCorrer());
				ins.setTipoComposicao(itemNptSumarioTemp.getTipoComposicao());
				ins.setQtdePrescrita(itemNptSumarioTemp.getQtdePrescrita());
				ins.setFormaDosagem(itemNptSumarioTemp.getFormaDosagem());
				ins.setComponenteNutricaoParental(itemNptSumarioTemp.getComponenteNutricaoParental());
				
				mpmItemNptSumarioDAO.persistir(ins);
				mpmItemNptSumarioDAO.flush();
			}
		}
		
		return ituSeq;
	}

	protected MpmItemPrescricaoSumarioDAO getMpmItemPrescricaoSumarioDAO(){
		return mpmItemPrescricaoSumarioDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected MpmItemMdtoSumarioDAO getMpmItemMdtoSumarioDAO(){
		return mpmItemMdtoSumarioDAO;
	}

	protected MpmItemCuidadoSumarioDAO getMpmItemCuidadoSumarioDAO(){
		return mpmItemCuidadoSumarioDAO;
	}
	
	protected MpmPrescricaoNptDAO getMpmPrescricaoNptDAO(){
		return mpmPrescricaoNptDAO;
	}
	
	protected MpmComposicaoPrescricaoNptDAO getMpmComposicaoPrescricaoNptDAO(){
		return mpmComposicaoPrescricaoNptDAO;
	}
	
	protected MpmItemPrescricaoNptDAO getMpmItemPrescricaoNptDAO(){
		return mpmItemPrescricaoNptDAO;
	}
	
	protected MpmItemNptSumarioDAO getMpmItemNptSumarioDAO(){
		return mpmItemNptSumarioDAO;
	}
	
	protected MpmSolicitacaoConsultoriaDAO getMpmSolicitacaoConsultoriaDAO(){
		return mpmSolicitacaoConsultoriaDAO;
	}
	
	protected MpmItemConsultoriaSumarioDAO getMpmItemConsultoriaSumarioDAO(){
		return mpmItemConsultoriaSumarioDAO;
	}
	
	protected MpmItemHemoterapiaSumarioDAO getMpmItemHemoterapiaSumarioDAO(){
		return mpmItemHemoterapiaSumarioDAO;
	}
	
	protected MpmPrescricaoProcedimentoDAO getMpmPrescricaoProcedimentoDAO(){
		return mpmPrescricaoProcedimentoDAO;
	}
	
	protected MpmModoUsoPrescProcedDAO getMpmModoUsoPrescProcedDAO(){
		return mpmModoUsoPrescProcedDAO;
	}
	
	protected MpmItemProcedimentoSumarioDAO getMpmItemProcedimentoSumarioDAO(){
		return mpmItemProcedimentoSumarioDAO;
	}

	protected MpmPrescricaoMdtoDAO getMpmPrescricaoMdtoDAO(){
		return mpmPrescricaoMdtoDAO;
	}
	
	protected MpmPrescricaoDietaDAO getMpmPrescricaoDietaDAO(){
		return mpmPrescricaoDietaDAO;
	}	

	protected MpmItemPrescricaoDietaDAO getMpmItemPrescricaoDietaDAO(){
		return mpmItemPrescricaoDietaDAO;
	}	
	
	protected MpmItemDietaSumarioDAO getMpmItemDietaSumarioDAO(){
		return mpmItemDietaSumarioDAO;
	}		
	
	protected MpmItemPrescricaoMdtoDAO getMpmItemPrescricaoMdtoDAO(){
		return mpmItemPrescricaoMdtoDAO;
	}
	
	protected MpmPrescricaoCuidadoDAO getMpmPrescricaoCuidadoDAO(){
		return mpmPrescricaoCuidadoDAO;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}
	
	protected IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}
}