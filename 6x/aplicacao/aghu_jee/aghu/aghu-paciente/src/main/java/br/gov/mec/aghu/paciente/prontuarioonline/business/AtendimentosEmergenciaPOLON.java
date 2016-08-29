package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioMes;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultHist;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosEmergenciaPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoAtendimentoEmergenciaPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class AtendimentosEmergenciaPOLON extends BaseBusiness {

	private static final String MASCARA_02D = "%02d";
	private static final String ACESSAR = "acessar";
	private static final String CODIGO_PACIENTE = "codigoPaciente";
	private static final String _HIFEN_ = " - ";
	private static final String NENHUM_REGISTRO="Nenhum registro.";
	private static final String NENHUM_REGISTRO_ICONE="/resources/img/icons/bin_closed.png";
	private static final String ICON_CLOCK = "/resources/img/icons/clock.png";
	private static final String ICON_ESP_MED1 = "/resources/img/icons/especialidades-medicas-1.png";
	private static final String ICON_ESP_MED2 = "/resources/img/icons/especialidades-medicas-2.png";
	private static final String ICON_TIME = "/resources/img/icons/time.png";
	private static final String ICON_AMBULATORIO = "/resources/img/icons/ambulatorio.png";
	
	
	@EJB
	private AtendimentosEmergenciaPOLRN atendimentosEmergenciaPOLRN;

	private static final Log LOG = LogFactory.getLog(AtendimentosEmergenciaPOLON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private static final long serialVersionUID = -2094044201027396502L;

	public List<AtendimentosEmergenciaPOLVO> pesquisarAtendimentosEmergenciaPOL(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer codigo, Date dthrInicio, Date dthrFim, Integer numeroConsulta, Short seqEspecialidade) {
		
		numeroConsulta = leastOneToNull(numeroConsulta);		
		
		Calendar dataFim = DateUtil.getCalendarBy(dthrFim);			
		Date dthrFinal = DateUtil.obterData(dataFim.get(Calendar.YEAR), dataFim.get(Calendar.MONTH), dataFim.getActualMaximum(Calendar.DAY_OF_MONTH), 0, 0);
		
		List<AtendimentosEmergenciaPOLVO> listaAtendimentosEmergenciaPOL = getAghuFacade().pesquisarAtendimentosEmergenciaPOL(firstResult, 
				maxResult, orderProperty, asc, codigo, dthrInicio, dthrFinal, numeroConsulta, seqEspecialidade);
		
//		List<AtendimentosEmergenciaPOLVO> listaAtendimentosEmergenciaPOL = getAghuFacade().pesquisarAtendimentosEmergenciaPOL(firstResult, 
//				maxResult, orderProperty, asc, codigo, stringToDate(dthrInicio), stringToDate(dthrFim), numeroConsulta, seqEspecialidade);
		
		for (AtendimentosEmergenciaPOLVO atendimentoVO: listaAtendimentosEmergenciaPOL){				
			//listaAtendimentosEmergenciaPOL.indexOf(atendimentoVO);
			if (listaAtendimentosEmergenciaPOL != null && !listaAtendimentosEmergenciaPOL.isEmpty()){
				
				if (atendimentoVO.getEspSeqPai() != null){

					AghEspecialidades espPai = getAghuFacade().obterAghEspecialidadesPorChavePrimaria(atendimentoVO.getEspSeqPai());
					atendimentoVO.setEspNomeEspecialidade(getAmbulatorioFacade().obterDescricaoCidCapitalizada(espPai.getNomeEspecialidade(), CapitalizeEnum.TODAS));
				}else{
					atendimentoVO.setEspNomeEspecialidade(getAmbulatorioFacade().obterDescricaoCidCapitalizada(atendimentoVO.getEspNomeEspecialidade(), CapitalizeEnum.TODAS));
				}

				atendimentoVO.setDataOrd(DateUtil.truncaData(atendimentoVO.getDthrInicio()));				

				// post-query			
				List<MamTrgEncInterno> trgEncInternoCons = getAmbulatorioFacade().obterPorConsultaOrderDesc(atendimentoVO.getNumero());			

				if (trgEncInternoCons == null || trgEncInternoCons.isEmpty()){
					atendimentoVO.setTrgSeq(null);				
				}else{				
					atendimentoVO.setTrgSeq(trgEncInternoCons.get(0).getTriagem().getSeq());
					atendimentoVO.setInicioTriagem(trgEncInternoCons.get(0).getTriagem().getDthrInicio());
					atendimentoVO.setFimTriagem(trgEncInternoCons.get(0).getTriagem().getDthrUltMvto());
					atendimentoVO.setIndPacAtendimento(trgEncInternoCons.get(0).getTriagem().getIndPacAtendimento());						
				}

				// MAMC_EMG_GET_ATD_SEQ

				atendimentoVO.setAtdSeq(getAtendimentosEmergenciaPOLRN().obterAtendimentoPorTriagem(atendimentoVO.getTrgSeq()));


				List<MpmAltaSumario> alta = getPrescricaoMedicaFacade().pesquisarAltaSumariosConcluidoAltaEObitoPorAtdSeq(atendimentoVO.getAtdSeq());


				// :vaa.atd_seq = atdSeq 
				// :vaa.trg_seq = trgSeq 
				// :vaa.inicio_triagem = inicioTriagem 

				if (alta != null && !alta.isEmpty() && alta.get(0).getDthrAlta() != null){	
					atendimentoVO.setAlta(alta.get(0).getDthrAlta());
					atendimentoVO.setIndSumAlta(Boolean.TRUE);
				}else{
					if (atendimentoVO.getIndPacAtendimento() == DominioPacAtendimento.N ){
						atendimentoVO.setAlta(atendimentoVO.getFimTriagem());
						atendimentoVO.setIndSumAlta(Boolean.FALSE);
					}else{
						atendimentoVO.setAlta(null);
						atendimentoVO.setIndSumAlta(Boolean.FALSE);
					}
				}
			}					
		}			
		 
		
		CoreUtil.ordenarLista(listaAtendimentosEmergenciaPOL, "dataOrd", false);
		CoreUtil.ordenarLista(listaAtendimentosEmergenciaPOL, "espNomeEspecialidade", true);
		CoreUtil.ordenarLista(listaAtendimentosEmergenciaPOL, "dthrInicio", false);
		
		Integer lastResult = (firstResult+maxResult)>listaAtendimentosEmergenciaPOL.size()?listaAtendimentosEmergenciaPOL.size():(firstResult+maxResult);
		
		listaAtendimentosEmergenciaPOL = listaAtendimentosEmergenciaPOL.subList(firstResult, lastResult);
		
		for(AtendimentosEmergenciaPOLVO vo : listaAtendimentosEmergenciaPOL){
			vo.setId(listaAtendimentosEmergenciaPOL.indexOf(vo));
		}
//		criteria.addOrder(Order.desc("t1." + AghAtendimentos.Fields.DTHR_INICIO.toString()));
//		criteria.addOrder(Order.asc("especialidade." + AghEspecialidades.Fields.NOME_ESPECIALIDADE_GENERICA.toString()));
//		criteria.addOrder(Order.desc("t1." + AghAtendimentos.Fields.DTHR_INICIO.toString()));
				
		return listaAtendimentosEmergenciaPOL;
		
	}


	public Long pesquisarAtendimentosEmergenciaPOLCount(Integer codigo, Date dthrInicio, Date dthrFim, Integer numeroConsulta, Short seqEspecialidade) {
		numeroConsulta = leastOneToNull(numeroConsulta);
		Calendar dataFim = DateUtil.getCalendarBy(dthrFim);			
		Date dthrFinal = DateUtil.obterData(dataFim.get(Calendar.YEAR), dataFim.get(Calendar.MONTH), dataFim.getActualMaximum(Calendar.DAY_OF_MONTH), 0, 0);
		return getAghuFacade().pesquisarAtendimentosEmergenciaPOLCount(codigo, dthrInicio, dthrFinal, numeroConsulta, seqEspecialidade);
//		return getAghuFacade().pesquisarAtendimentosEmergenciaPOLCount(codigo, stringToDate(dthrInicio), stringToDate(dthrFim), numeroConsulta, seqEspecialidade);
	}
	
	public String leastOneToNull(String str){
		if("-1".trim().equals(str)){
			return null;
		}else{
			return str;
		}
	}
	public Integer leastOneToNull(Integer inte){
		if(inte == null || -1 == inte){
			return null;
		}else{
			return inte;
		}
	}
	
 	public Boolean habilitarBotaoEvolucaoAnamnese(AtendimentosEmergenciaPOLVO registroSelecionado) {
		Boolean retorno;
 		
 		if (registroSelecionado.getTrgSeq() == null ){	//não existe Triagem (List<MamTrgEncInterno> trgEncInternoCons = null)
			retorno = Boolean.FALSE;
		}else{
			if (!getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "acessoAdminPol", ACESSAR) 
					|| getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "acessoConsEmgPOL", ACESSAR)) {
				retorno = Boolean.TRUE;
			}else{
				retorno = verificaSeAtendimentoEmAndamentoEImpressao(registroSelecionado.getAtdSeq(), registroSelecionado.getTrgSeq());
			}
		}
 		
 		return retorno;
 	}
 	
 	public Boolean habilitarBotaoConsultoria(AtendimentosEmergenciaPOLVO registroSelecionado) {
		Boolean retorno = Boolean.FALSE;
		
		if (registroSelecionado.getTrgSeq() == null ){	//não existe Triagem (List<MamTrgEncInterno> trgEncInternoCons = null)
			retorno = Boolean.FALSE;
		}else{
			if (!getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "acessoAdminPol", ACESSAR) 
					|| getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "acessoConsEmgPOL", ACESSAR)) {
				retorno = verificaSeTemMpmSolicitacaoConsultoria(registroSelecionado.getAtdSeq());
			}
		}
		return retorno;
 	}
	
	public Boolean habilitarBotaoSumarioAlta(AtendimentosEmergenciaPOLVO registroSelecionado) {
		
		if (registroSelecionado.getTrgSeq() == null ){	//não existe Triagem (List<MamTrgEncInterno> trgEncInternoCons = null)
			return false;
		}else{
			return registroSelecionado.getIndSumAlta();
		}
	}
	
	//p_ver_atd
 	private Boolean verificaSeAtendimentoEmAndamentoEImpressao(Integer atdSeq,
			Long trgSeq) {
 		AghAtendimentos atendimento = null;
		if(atdSeq != null){
			atendimento = getAghuFacade().obterAtendimentoEmAndamento(atdSeq);
		}
		if ((atendimento != null)){
			// MAMP_ATU_TMP_TRIAGEM
			Boolean imprime = getAtendimentosEmergenciaPOLRN().verificarImpressao(trgSeq,null);
			return imprime;
		}else{
			return Boolean.FALSE;
		}
	}


 	//p_ver_consultoria
	private Boolean verificaSeTemMpmSolicitacaoConsultoria(
			Integer atdSeq) {
		Boolean retorno;
		if(atdSeq == null){
			retorno = Boolean.FALSE;
		}else{ //verifica se existe consultoria
			List<MpmSolicitacaoConsultoria> consultoria = getPrescricaoMedicaFacade().obterConsultoriaAtiva(atdSeq);			
			if ((consultoria != null && !consultoria.isEmpty())){	
				retorno = Boolean.TRUE;
			}else{				
				List<MpmSolicitacaoConsultHist> consultoriaHist = getPrescricaoMedicaFacade().obterHistoricoConsultoria(atdSeq);				
				if ((consultoriaHist != null && !consultoriaHist.isEmpty())){	
					retorno = Boolean.TRUE;
				}else{
					retorno = Boolean.FALSE;
				}
			}	
		}
		return retorno;
	}
	
	private AtendimentosEmergenciaPOLRN getAtendimentosEmergenciaPOLRN(){
		return atendimentosEmergenciaPOLRN;
	}
		

//	protected MpmSolicitacaoConsultoriaDAO getMpmSolicitacaoConsultoriaDAO(){
//		return mpmSolicitacaoConsultoriaDAO;
//	}
		
	protected IAmbulatorioFacade getAmbulatorioFacade(){
		return this.ambulatorioFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return this.prescricaoMedicaFacade;
	}
	
	private IPermissionService getPermissionService() {
		return this.permissionService;
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void processarNodosEmergenciaPol(NodoPOLVO nodoEmergencia, Integer codPaciente) throws ApplicationBusinessException {
		 Integer 	vLimTab = 0;
		 Integer 	vAno;
		 Integer 	vMes;
		 String 	vData;
		 Integer 	vPeriodoAtual;
		 Integer 	vPeriodoAnt = 0;
		 String 	vNome;

		vData = DateUtil.dataToString(new Date(), "yyyyMM");
		NodoEmergencia [] tabData = new NodoEmergencia[4];
		tabData[1] = new NodoEmergencia();
		tabData[2] = new NodoEmergencia();
		tabData[3] = new NodoEmergencia();
		
		tabData[1].dataFinal = vData; 
		vAno = Integer.valueOf(vData.substring(0,4));
		vMes = Integer.valueOf(vData.substring(4,6));
		
		if(vMes <=5){
			vAno--;
			vMes = 12-5+vMes;
		}else{
			vMes -=5;
		}
		
		vData = vAno + String.format(MASCARA_02D, vMes);
		tabData[1].dataInicial = vData;
		
		if(vMes == 1){
			vAno -=1;
			vMes = 12;
		}else{
			vMes -= 1;
		}
		
		vData = vAno + String.format(MASCARA_02D, vMes);
		tabData[2].dataFinal = vData;
		
		if(vMes <=5){
			vAno-=1;
			vMes = 12-5+vMes;
		}else{
			vMes -=5;
		}
		
		vData = vAno + String.format(MASCARA_02D, vMes);
		tabData[2].dataInicial = vData;
		
		if(vMes ==1){
			vAno -=1;
			vMes = 12;
		}else{
			vMes -=1;
		}
		
		vData = vAno + String.format(MASCARA_02D, vMes);
		tabData[3].dataFinal = vData;
		
		tabData[3].dataInicial = "190001";
		
		vLimTab = 3;
		
		for(int vCont =1; vCont <= vLimTab; vCont++){
			if(vCont == vLimTab){
				tabData[vCont].descricao = "Até " + cMontaDescPeriodo(tabData[vCont].dataFinal);
			}else{
				tabData[vCont].descricao = cMontaDescPeriodo(tabData[vCont].dataInicial) + " até " + cMontaDescPeriodo(tabData[vCont].dataFinal); 
			}
		}
		//String iconeConstrucao = "/images/icons/construction.png";
		
		int idx=1;
		
		//-- CRIAÇÃO DO NODO CRONOLOGIA ITEM 2
		NodoPOLVO nodoOrdemCronologica = new NodoPOLVO(nodoEmergencia.getProntuario(),"emergenciaOrdemCronologica", "Cronológica", ICON_CLOCK, idx++, null);
		
		//CRIAÇÃO DO NÓ ESPECIALIDADE
		NodoPOLVO nodoEspecialidadeEmergencia = new NodoPOLVO(nodoEmergencia.getProntuario(),"especialidadesEmergencia", "Especialidade", ICON_ESP_MED1, 1, null);
		List<NodoAtendimentoEmergenciaPOLVO> nodosEmergencia = getAghuFacade().pesquisarNodosMenuAtendimentosEmergenciaPol(codPaciente);
		
		vPeriodoAnt = 0;
		String nomeEspecialidade = "";
		Map<String, NodoPOLVO> mapNivel3 = new TreeMap<String, NodoPOLVO>();
		Map<String, Map<String,NodoPOLVO>> mapNivel4 = new HashMap<String, Map<String,NodoPOLVO>>();
		Map<String, String> mapServidor = new HashMap<>();
		
		//NodoPOLVO nodoPeriodoEmerg = null;
		for(NodoAtendimentoEmergenciaPOLVO att: nodosEmergencia){
			
			vPeriodoAtual = cDeterminaPeriodo(att.getDtHrInicio(), tabData); 
			if(vPeriodoAtual != vPeriodoAnt){
				//-- CRIAÇÃO DOS NODOS DE PERIODO ITEM 3
				NodoPOLVO nodoNivel3 = new NodoPOLVO(nodoEmergencia.getProntuario(),"emergenciaOrdemCronologicaPeriodo", tabData[vPeriodoAtual].descricao, ICON_TIME, idx++, nodoEmergencia.getPagina());
				
				nodoNivel3.addParam("dataInicial", att.getDataInicioNodoNivel3());
				nodoNivel3.addParam("dataFinal", att.getDataFinalNodoNivel3());
				nodoNivel3.addParam(CODIGO_PACIENTE, codPaciente);
				
				nodoOrdemCronologica.getNodos().add(nodoNivel3);
				vPeriodoAnt = vPeriodoAtual;
			}
			
			vNome = getAmbulatorioFacade().obterDescricaoCidCapitalizada(getNomeConsProf(att.getSerVinCodigo(),att.getSerVinMatricula(), mapServidor), CapitalizeEnum.TODAS);
			StringBuffer nomeNodo = new StringBuffer(DateUtil.dataToString(att.getDtHrInicio(), "dd/MM/yyyy"));
			nomeNodo.append(_HIFEN_)
			.append(att.getSigla())
			.append(_HIFEN_)
			.append(vNome);
			
			NodoPOLVO nodoNivel41 = new NodoPOLVO(nodoEmergencia.getProntuario(),"folhaEmergenciaOrdemCronologicaPeriodo", nomeNodo.toString(), ICON_AMBULATORIO, idx++, nodoEmergencia.getPagina());
			nodoNivel41.setQuebraLinha(true);
			nodoNivel41.addParam("dataInicial", att.getDtHrInicio());			
			nodoNivel41.addParam(CODIGO_PACIENTE, codPaciente);
			nodoNivel41.addParam("numeroConsulta", att.getConNumero());
			
			nodoOrdemCronologica.getNodos().get(nodoOrdemCronologica.getNodos().size()-1).getNodos().add(nodoNivel41);			
			nomeEspecialidade = getAmbulatorioFacade().mpmcMinusculo(att.getNomeEspecialidade(), 2);
			
			NodoPOLVO nodoNivel3 = new NodoPOLVO(nodoEmergencia.getProntuario(),"emergenciaNomeEspecialidade", nomeEspecialidade, ICON_ESP_MED2,idx++, nodoEmergencia.getPagina());
			nodoNivel3.addParam(CODIGO_PACIENTE, codPaciente);
			nodoNivel3.addParam("seqEspecialidade", att.getSeqEspecialidade());			
			
			if(!mapNivel3.containsKey(nomeEspecialidade)){
				mapNivel3.put(nomeEspecialidade, nodoNivel3);
			}
			
			vPeriodoAtual = cDeterminaPeriodo(att.getDtHrInicio(), tabData);
			NodoPOLVO nodoNivel4 = new NodoPOLVO(nodoEmergencia.getProntuario(),"emergenciaOrdemEspecialidadePeriodo", tabData[vPeriodoAtual].descricao, ICON_TIME, idx++, nodoEmergencia.getPagina());
			
			nodoNivel4.addParam("dataInicial", att.getDataInicioNodoNivel3());
			nodoNivel4.addParam("dataFinal", att.getDataFinalNodoNivel3());
			nodoNivel4.addParam(CODIGO_PACIENTE, codPaciente);
			nodoNivel4.addParam("seqEspecialidade", att.getSeqEspecialidade());
			
			//mapeia o nodo de periodo dentro da especialidade
			if(mapNivel4.containsKey(nomeEspecialidade)){
				
				//Só adiciona se não tem o periodo mapeado
				if(!mapNivel4.get(nomeEspecialidade).containsKey(tabData[vPeriodoAtual].descricao)){
					mapNivel4.get(nomeEspecialidade).put(tabData[vPeriodoAtual].descricao, nodoNivel4);
				}
			}
			else{
				//nao tem a especialidade mapeada, adiciona já com o nodo
				mapNivel4.put(nomeEspecialidade, new HashMap<String, NodoPOLVO>());
				mapNivel4.get(nomeEspecialidade).put(tabData[vPeriodoAtual].descricao, nodoNivel4);
			}
		}		
		nodoEmergencia.getNodos().add(nodoOrdemCronologica);

		//percorre a lista novamente, agora colocando os nós 5 aos nivel 4, e estes ao nivel 3
		for(NodoAtendimentoEmergenciaPOLVO att: nodosEmergencia){
			vPeriodoAtual = cDeterminaPeriodo(att.getDtHrInicio(), tabData);
			nomeEspecialidade = getAmbulatorioFacade().mpmcMinusculo(att.getNomeEspecialidade(), 2);
			if(mapNivel4.containsKey(nomeEspecialidade) && 
					mapNivel4.get(nomeEspecialidade).containsKey(tabData[vPeriodoAtual].descricao)){
				
				NodoPOLVO nodoNivel3 = mapNivel3.get(nomeEspecialidade);
				
				NodoPOLVO nodoNivel4 = mapNivel4.get(nomeEspecialidade).get(tabData[vPeriodoAtual].descricao);
				
				vNome = getAmbulatorioFacade().obterDescricaoCidCapitalizada(getNomeConsProf(att.getSerVinCodigo(),att.getSerVinMatricula(), mapServidor), CapitalizeEnum.TODAS);				
				
				StringBuffer nomeNodo = new StringBuffer(DateUtil.dataToString(att.getDtHrInicio(), "dd/MM/yyyy"));
				nomeNodo.append(_HIFEN_)
				.append(att.getSigla())
				.append(_HIFEN_)
				.append(vNome);
				
				NodoPOLVO nodoNivel5 = new NodoPOLVO(nodoEmergencia.getProntuario(),"emergenciaEspecialidadePeriodo", nomeNodo.toString(), ICON_AMBULATORIO, idx++, nodoEmergencia.getPagina());
				nodoNivel5.setQuebraLinha(true);
				nodoNivel5.addParam(CODIGO_PACIENTE, codPaciente);
				nodoNivel5.addParam("seqEspecialidade", att.getSeqEspecialidade());
				nodoNivel5.addParam("numeroConsulta", att.getConNumero());

				nodoNivel4.getNodos().add(nodoNivel5);
				
				if(nodoNivel3.getNodos() == null){
					nodoNivel3.setNodos(new ArrayList<NodoPOLVO>());
				}
				if(!nodoNivel3.getNodos().contains(nodoNivel4)){
					nodoNivel3.getNodos().add(nodoNivel4);
				}
			}
		}
		
		if(nodoEspecialidadeEmergencia.getNodos() == null){
			nodoEspecialidadeEmergencia.setNodos(new ArrayList<NodoPOLVO>());
		}
		//Adiciona todos os nós do nível 3				
		nodoEspecialidadeEmergencia.getNodos().addAll(mapNivel3.values());
		
		//Adiciona o nó especialidade ao nó emergencia
		nodoEmergencia.getNodos().add(nodoEspecialidadeEmergencia);
		
		NodoPOLVO nenhumRegistro = new NodoPOLVO(nodoEmergencia.getProntuario(),NENHUM_REGISTRO, NENHUM_REGISTRO, NENHUM_REGISTRO_ICONE, true);	
		if (nodoOrdemCronologica.getNodos().isEmpty()){
			nodoOrdemCronologica.getNodos().add(nenhumRegistro);
		}
		if (nodoEspecialidadeEmergencia.getNodos().isEmpty()){
			nodoEspecialidadeEmergencia.getNodos().add(nenhumRegistro);
		}
		
	}
	
	
	private String getNomeConsProf(Short codigo, Integer matricula, Map<String, String> mapServidor) throws ApplicationBusinessException{
		String mapKey=codigo + "-" + matricula;
		if (mapServidor.containsKey(mapKey)){
			return mapServidor.get(mapKey);
		}else{
			RapServidores rapServidor = getRegistroColaboradorFacade().buscarServidor(codigo, matricula);
			Object [] buscaConsProf = getPrescricaoMedicaFacade().buscaConsProf(rapServidor);
			String nome = (String) buscaConsProf[1];
			mapServidor.put(mapKey, nome);
			return nome ;
		}	
	}
	
	
	public Date stringToDate(String data, String pattern){
		if(pattern == null){
			pattern = "yyyyMM";
		}
		
		if(data == null){
			return null;
		}
		
		try{
			return new Date(new SimpleDateFormat(pattern).parse(data).getTime());
		}catch (ParseException e) {
			return null;
		}
	}
	
	public Date stringToDate(String data){
		return stringToDate(data, null);
	}


	private Integer cDeterminaPeriodo(Date dataInicio, NodoEmergencia[] tabData) {
		String  pAnoMes = DateUtil.dataToString(dataInicio, "yyyyMM");
		Integer pAnoMesInt = Integer.parseInt(pAnoMes);
		
		Integer vLimTab = 3;
		
		Integer vPeriodo = 0;
		for(int vCont =1; vCont <= vLimTab; vCont++){
			Integer dataInicialInt = Integer.parseInt(tabData[vCont].dataInicial);
			Integer dataFinalInt = Integer.parseInt(tabData[vCont].dataFinal);
			if(pAnoMesInt >= dataInicialInt && pAnoMesInt <= dataFinalInt){
				vPeriodo = vCont;
			}
		}
		
		if(vPeriodo == 0){
			vPeriodo = 3;
		}
		
		return vPeriodo;
	}

	@SuppressWarnings("PMD.UnusedPrivateMethod")
	private String cMontaDescPeriodo(String data) {
		Integer vAno = Integer.parseInt(data.substring(0,4));
		Integer vMes = Integer.parseInt(data.substring(4,6));
		
		DominioMes mes = DominioMes.obterDominioMes(vMes-1);
		
		return getAmbulatorioFacade().obterDescricaoCidCapitalizada(mes.getDescricao()) + "/" + vAno;
	}

	class NodoEmergencia{
		private String dataInicial;
		private String dataFinal;
		private String descricao;
		
		public String getDataInicial() {
			return dataInicial;
		}
		public String getDataFinal() {
			return dataFinal;
		}
		public String getDescricao() {
			return descricao;
		}
		public void setDataInicial(String dataInicial) {
			this.dataInicial = dataInicial;
		}
		public void setDataFinal(String dataFinal) {
			this.dataFinal = dataFinal;
		}
		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}
	
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
}