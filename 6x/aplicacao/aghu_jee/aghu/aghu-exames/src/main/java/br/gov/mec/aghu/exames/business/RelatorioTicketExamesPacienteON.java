package br.gov.mec.aghu.exames.business;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelRecomendacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExamesHistDAO;
import br.gov.mec.aghu.exames.vo.TicketExamesPacienteVO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelRecomendacaoExame;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.IAelSolicitacaoExames;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioTicketExamesPacienteON extends BaseBusiness {

private static final String GPP_ = "GPP-";

private static final String _HIFEN_ = " - ";

private static final String QUEBRA_LINHA = "\r\n";

private static final Log LOG = LogFactory.getLog(RelatorioTicketExamesPacienteON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AelRecomendacaoExameDAO aelRecomendacaoExameDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@Inject
	private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private AelSolicitacaoExamesHistDAO aelSolicitacaoExamesHistDAO;
	
	@EJB
	private PesquisarRelatorioTicketExamesPacienteON pesquisarRelatorioTicketExamesPacienteON;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8902277364189911247L;

	public List<TicketExamesPacienteVO> pesquisarRelatorioTicketExamesPaciente(
			Integer codSolicitacao, Short unfSeq, Set<Short> listaUnfSeq)  throws ApplicationBusinessException {
		
		return getPesquisarRelatorioTicketExamesPacienteON().pesquisarRelatorioTicketExamesPaciente(codSolicitacao, unfSeq, listaUnfSeq);
	}
	
	public void pesquisarUnidadeFuncionalColeta()  throws ApplicationBusinessException {
		 getPesquisarRelatorioTicketExamesPacienteON().pesquisarUnidadeFuncionalColeta();
	}
	
	
	public Boolean validarImpressaoExamePelaUnidade(AelItemSolicitacaoExames itemSolicitacao, Short unfExec, AelMateriaisAnalises materialAnalise) {
		List<Short> unfHierarquico = this.getAghuFacade().obterUnidadesFuncionaisHierarquicasPorCaract(itemSolicitacao.getUnidadeFuncional().getSeq());
		Boolean unidColeta = getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(unfExec, ConstanteAghCaractUnidFuncionais.UNID_COLETA);
		Boolean unidExecExames = getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(unfExec, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_EXAMES);
		Boolean unidadeContidaEmUnfHierarquico = unfHierarquico.contains(unfExec);
		if(!((unidColeta && materialAnalise.getIndColetavel())
				|| (unidExecExames && unidadeContidaEmUnfHierarquico))) {
			return true;
		}
		return false;
	}
	
	public void organizarPorMaterialComparece(
			List<TicketExamesPacienteVO> tickets) {
		String material = "";
		Boolean algumTemAgendamento = false;
		Boolean algumNaoTemAgendamento = false;
//		String agendamento = "";
//		String comparece = "";
		for(TicketExamesPacienteVO ticket : tickets){
			if(!material.equals(ticket.getDescricaoMaterial())){
				material = ticket.getDescricaoMaterial();
				
				StringBuffer itensComAgendamento = new StringBuffer();
				for(TicketExamesPacienteVO ticket2 : tickets){
					if(ticket2.getDescricaoMaterial().equals(material)){
						if(!ticket2.getAgendamento().equals("") ){
							algumTemAgendamento = true;
//							agendamento = ticket2.getAgendamento();
							itensComAgendamento.append("Item ").append(ticket2.getItemSolicitacaoExameSeqP()).append(' ');//break;
						}
					}
				}
				
				for(TicketExamesPacienteVO ticket2 : tickets){
					if(ticket2.getDescricaoMaterial().equals(material)){
						if(ticket2.getAgendamento().equals("") ){
							algumNaoTemAgendamento = true;
//							comparece = ticket2.getTextoComparecer();
							break;
						}
					}
				}
				
				if(algumNaoTemAgendamento || algumTemAgendamento){
					for(TicketExamesPacienteVO ticket2 : tickets){
						if(ticket2.getDescricaoMaterial().equals(material)){
							if(algumTemAgendamento){
//								ticket2.setAgendamento(agendamento);
								Boolean simNao = 
									//getAghCaractUnidFuncionaisDAO().verificarCaracteristicaDaUnidadeFuncional(Short.valueOf(ticket2.getUnfSeqComparece()), ConstanteAghCaractUnidFuncionais.AREA_FECHADA);
									getAghuFacade().verificarCaracteristicaUnidadeFuncional(Short.valueOf(ticket2.getUnfSeqComparece()), ConstanteAghCaractUnidFuncionais.AREA_FECHADA);
									
								if(simNao.equals(Boolean.TRUE)){
									ticket2.setExamesAgendados(itensComAgendamento.toString());
								}
							}
							
//							if(algumNaoTemAgendamento) {
//								ticket2.setTextoComparecer(comparece);
//							}
						}
					}
					
					algumNaoTemAgendamento = false;
					algumTemAgendamento = false;
//					agendamento = "";
//					comparece = "";
				}
			}
			
		}
		
	}


	/**
	 * oradb function M_7FormatTrigger
	 * @param informacoesClinicas
	 * @param unfSeq
	 * @return
	 */
	public String buscarInformacoesClinicas(String informacoesClinicas, String unfSeq) {
		AghCaractUnidFuncionais caract = 
			//getAghCaractUnidFuncionaisDAO().buscarCaracteristicaPorUnidadeCaracteristica(Short.valueOf(unfSeq),ConstanteAghCaractUnidFuncionais.UNID_COLETA);
			this.getAghuFacade().buscarCaracteristicaPorUnidadeCaracteristica(Short.valueOf(unfSeq),ConstanteAghCaractUnidFuncionais.UNID_COLETA);
		
		if(caract != null){
			return "";
		}else{
			return informacoesClinicas;
		}		
	}


	public String buscarNovasRecomendacoes() {
		
		AghParametros aghParametros = null;
		String vlrTexto = null;
		try {
			aghParametros = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_NOVAS_RECOMENDACOES);
		
		vlrTexto = aghParametros.getVlrTexto();
		} catch (ApplicationBusinessException e) {
			vlrTexto = "N";
		}
		return vlrTexto;
	}


	/**
	 * oradb function CF_AMO_BARRASFormula
	 * @param itemSolicitacao
	 * @return
	 *  
	 */
	public String buscarCodigoBarraItem(
			AelItemSolicitacaoExames itemSolicitacao) throws ApplicationBusinessException {
		
		List<AelAmostraItemExames> listItemAmostra = getAelAmostraItemExamesDAO().buscarAelAmostraItemExamesAelAmostrasPorItemSolicitacaoExame(itemSolicitacao);
		if(listItemAmostra != null && !listItemAmostra.isEmpty()){
			AelMateriaisAnalises manSeq = listItemAmostra.get(0).getAelAmostras().getMateriaisAnalises();
			
			AghParametros aghParametros = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_COD_SANGUE);
			Short codSangue = aghParametros.getVlrNumerico().shortValue();
			
			if(codSangue.equals(manSeq.getSeq().shortValue())){
				return "";
			}
			
		}else{
			return "";
		}
		
		List<AelAmostraItemExames> amostra = getAelAmostraItemExamesDAO().buscarAelAmostraItemExamesPorItemSolicitacaoExame(itemSolicitacao);
		if(amostra != null && !amostra.isEmpty()){
			Integer amoSoeSeq = amostra.get(0).getId().getAmoSoeSeq();
			Integer amoSeqp = amostra.get(0).getId().getAmoSeqp();
			
			
			StringBuffer soeSeq = new StringBuffer(amoSoeSeq.toString());
			Integer difSoeSeq = 7 - soeSeq.length();
			for(int i=0; i < difSoeSeq; i++){
				soeSeq.insert(0, "0");
			}
			soeSeq = new StringBuffer(soeSeq.toString().trim());
			
			StringBuffer seqp = new StringBuffer(amoSeqp.toString());
			Integer difSeqp = 3 - seqp.length();
			for(int i=0; i < difSeqp; i++){
				seqp.insert(0, "0");
			}
			//seqp = seqp.substring(2, 4);
			return soeSeq.append(seqp).toString();
		}
		return "";
	}


	public void verificarSetarMaiorTempoRealizacao(
			List<TicketExamesPacienteVO> tickets) {
		Integer maior = 0;
		for(TicketExamesPacienteVO vo : tickets){
			Integer tempoRealizacao = vo.getTempoRealizacaoDias() != null ? vo.getTempoRealizacaoDias() : 0;
			if(tempoRealizacao > maior){
				maior = tempoRealizacao;
			}
		}
		
		for(TicketExamesPacienteVO vo : tickets){
			vo.setMaiorTempoRealizacao(maior.toString());
		}
		
	}
	
	public void verificarSetarMaiorTempoJejum(List<TicketExamesPacienteVO> tickets) {
		StringBuilder result = new StringBuilder(20);
		Short maiorJejum = 0;
		for(TicketExamesPacienteVO vo : tickets){
			if(vo.getTempoJejum()!=null && maiorJejum < vo.getTempoJejum()) {
				maiorJejum = vo.getTempoJejum();
			}
		}
		
		if (maiorJejum > 0) {
			result.append("Jejum: " + maiorJejum + " " + (maiorJejum > 1 ? "horas" : "hora"));
		} else {
			result.append("Não necessita Jejum");
		}
		
		for(TicketExamesPacienteVO vo : tickets){
			vo.setMaiorTempoJejum(result.toString());
		}	
	}


	/**
	 * oradb function cf_unidcompareceFormula
	 * 
	 * @param itemSolicitacao
	 * @param caractUnidFuncionais 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String buscarTextoComparecer(
			AelItemSolicitacaoExames itemSolicitacao, AghCaractUnidFuncionais caractUnidFuncionais) throws ApplicationBusinessException {
		
		StringBuffer vDescricao = new StringBuffer(50);
		String vUnidade = "";

		AghUnidadesFuncionais unidSolicitante = itemSolicitacao.getSolicitacaoExame().getUnidadeFuncional();
		AghUnidadesFuncionais unidFuncComparece = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(itemSolicitacao.getAelUnfExecutaExames().getUnfSeqComparece().getSeq()) ;
		
	    String indAla = unidFuncComparece.getIndAla() != null ? unidFuncComparece.getIndAla().getDescricao() : "";    
		vUnidade = unidFuncComparece.getDescricao()+_HIFEN_+unidFuncComparece.getAndar()+"o Andar/"+indAla;
		
		AghParametros aghParametros = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_UNIDADE_UBS);
		Short codUnidadeBasicaSaude = aghParametros.getVlrNumerico().shortValue();		
		aghParametros = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_COD_UNIDADE_COLETA_DEFAULT);		
		Short codUnidadeColeta = aghParametros.getVlrNumerico().shortValue();

		if(unidFuncComparece.getSeq().equals(codUnidadeColeta) && unidSolicitante.getSeq().equals(codUnidadeBasicaSaude)){
			vUnidade = "Recepção da Unidade Básica de Saúde";
		}
		
		AelItemHorarioAgendado aelItemHorarioAgendado = getAelItemHorarioAgendadoDAO().buscarAelItemHorarioAgendadoComGradePorItemSolicitacaoExame(itemSolicitacao);		
		if(aelItemHorarioAgendado != null ){
			vDescricao.append("Comparecer na: ").append(vUnidade);
			
			if(getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(caractUnidFuncionais != null ? caractUnidFuncionais.getUnidadeFuncional().getSeq() : codUnidadeColeta,ConstanteAghCaractUnidFuncionais.UNID_COLETA) ){
				vDescricao.append(" - Esteja 15 min. antes do horário agendado.");
			}else{
				vDescricao.append('.');
			}
		}else{
			vDescricao.append("Comparecer na ").append(vUnidade);
			vDescricao.append(" para agendar a data e hora do seu exame.");
		}
		
		return vDescricao.toString();
	}


	public String buscarAgendamentoItemExame(
			AelItemSolicitacaoExames itemSolicitacao) {
		AelItemHorarioAgendado aelItemHorarioAgendado = getAelItemHorarioAgendadoDAO().buscarAelItemHorarioAgendadoComGradePorItemSolicitacaoExame(itemSolicitacao);
		String dia = "";
		String sala = "";
		if(aelItemHorarioAgendado != null){
			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yy HH:mm");
			dia = "Dia: "+sdf1.format(aelItemHorarioAgendado.getId().getHedDthrAgenda());
			if(aelItemHorarioAgendado.getHorarioExameDisp().getGradeAgendaExame().getSalaExecutoraExames() != null) {
				sala = "Sala: "+aelItemHorarioAgendado.getHorarioExameDisp().getGradeAgendaExame().getSalaExecutoraExames().getNumero();
			}
			return dia+"     "+sala;
		}
		return "";
	}


	public String buscarRecomendacoesExames(String sigla, Integer manSeq, String novasRecomendacoes) {
		if("N".equals(novasRecomendacoes)){
			List<AelRecomendacaoExame> lista = getAelRecomendacaoExameDAO().obterRecomendacoesExameResponsavelAbrangencia(sigla, manSeq);//novasRecomendacoes

            StringBuffer buffer = new StringBuffer();

			for(AelRecomendacaoExame re : lista){
                buffer.append(re.getDescricao()).append(QUEBRA_LINHA);
			}

			if(buffer.length() > 0){
				return buffer.toString();
			}
		}
		return "";
	}


	/**
	 * ORADB: AELC_GET_PROJ_ATEND
	 *  ESCHWEIGERT -> (23/04/2012) Deve ser público e mapeado para outros programas poderem utilizar.
	 * @param atendimento
	 * @param atendimentoDiversos
	 * @return
	 */
	public String aelcGetProjAtend(final Integer atdSeq, final Integer atvSeq) {
		AghAtendimentos atendimento = null;
		if(atdSeq != null){
			atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);
		}
		
		AelAtendimentoDiversos atendimentoDiversos = null;
		if(atvSeq != null){
			atendimentoDiversos= getExamesFacade().obterAelAtendimentoDiversosPorChavePrimaria(atvSeq);
		}
		
		return aelcGetProjAtend(atendimento, atendimentoDiversos);
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	

	/**
	 * ORADB: AELC_GET_PROJ_ATEND
	 *  ESCHWEIGERT -> (23/04/2012) Deve ser público e mapeado para outros programas poderem utilizar.
	 * @param atendimento
	 * @param atendimentoDiversos
	 * @return
	 */
	public String aelcGetProjAtend(AghAtendimentos atendimento, AelAtendimentoDiversos atendimentoDiversos) {
		AelProjetoPesquisas projeto = null;
		
		if (atendimento != null) {
			
			if (atendimento.getOrigem().equals(DominioOrigemAtendimento.I)) {
				AinInternacao internacao = atendimento.getInternacao();
				projeto = internacao.getProjetoPesquisa();
				
			} else if (atendimento.getOrigem().equals(DominioOrigemAtendimento.A) && atendimento.getConsulta() != null) { 
				
				AacConsultas consulta =  atendimento.getConsulta();
				projeto = consulta.getProjetoPesquisa();
				
			} else if(atendimento.getOrigem().equals(DominioOrigemAtendimento.C)){
				
				List<MbcCirurgias> cirurgias = getBlocoCirurgicoFacade().pesquisarCirurgiaPorAtendimento(atendimento.getSeq());
				MbcCirurgias cirurgia = null;
				
				if (cirurgias != null && !cirurgias.isEmpty()) {
					
					cirurgia = cirurgias.get(cirurgias.size()-1);
					projeto = cirurgia.getProjetoPesquisa();
					
				}
				
			}
			
		} else {
			
			projeto =  atendimentoDiversos.getAelProjetoPesquisas();
		
		}
		
		String projetoDesc = null;
		
		if (projeto != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(projeto.getNumero());
				
			if (projeto.getNome() != null) {
				sb.append(_HIFEN_).append(projeto.getNome());
			}
			
			projetoDesc = sb.toString();
		}
		
		return projetoDesc;
	}


	/**
	 * oradb aelc_busca_conv_plan
	 * 
	 * @param seq 
	 * @param cnvCodigo 
	 * @return
	 */
	public String buscarConvenioPlano(FatConvenioSaudePlano convenioSaudePlano) {
		if(convenioSaudePlano != null && convenioSaudePlano.getConvenioSaude() != null){
			FatConvenioSaude convenioSaude = convenioSaudePlano.getConvenioSaude();
			return convenioSaude.getDescricao()+" / "+convenioSaudePlano.getDescricao();
		}
		
		return null;
	}


	/**
	 * Obtem a unidade funcional de coleta.
	 * 
	 * @return
	 *  
	 */
	public AghUnidadesFuncionais buscarUnidadeFuncionalColeta() throws ApplicationBusinessException {
		AghParametros aghParametros = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_COD_UNIDADE_COLETA_DEFAULT);
		Short vlrNumerico = aghParametros.getVlrNumerico().shortValue();
		AghUnidadesFuncionais unidadeFuncionalColeta = 
			//getAghUnidadesFuncionaisDAO().obterPeloId(vlrNumerico);
			this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(vlrNumerico);
		return unidadeFuncionalColeta;
	}
	
	/**
	 * oradb aelc_busca_nome_serv
	 * 
	 * @return
	 */
	public String buscarNomeServ(Integer matricula, Short vinCodigo){
		if(matricula != null){
			RapServidores serv = getRegistroColaboradorFacade().obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(matricula,vinCodigo);
			RapPessoasFisicas pessoa = serv.getPessoaFisica();
			if(pessoa != null){
				return pessoa.getNome();
			}else{
				return "";
			}
		}
		return "";
	}
	
	public String buscarLaudoProntuarioPaciente(IAelSolicitacaoExames solicitacaoExames) {
		if (solicitacaoExames instanceof AelSolicitacaoExames) {
			return buscarLaudoProntuarioPaciente((AelSolicitacaoExames) solicitacaoExames);
		} else {
			return buscarLaudoProntuarioPacienteHist((AelSolicitacaoExamesHist) solicitacaoExames);
		}
	}
	
	public String buscarNroRegistroConselho(Short vinCodigo, Integer matricula, boolean verificaDataFimVinculo){
		if(matricula != null){
			String nroRegistroConselhor = getRegistroColaboradorFacade().buscarNroRegistroConselho(vinCodigo, matricula, Boolean.FALSE);
			//RapQualificacao CRM;
			if(nroRegistroConselhor != null){
				return nroRegistroConselhor;
			}else{
				return "";
			}
		}
		return "";
	}
	
	/**
	 * oradb AELC_LAUDO_PRNT_PAC
	 * @HIST RelatorioTicketExamesPacienteON.buscarLaudoProntuarioPacienteHist
	 */
	public String buscarLaudoProntuarioPaciente(AelSolicitacaoExames solicitacaoExames) {
		aelSolicitacaoExameDAO.refresh(solicitacaoExames);
		return buscaProntuario(solicitacaoExames);
	}

	public String buscaProntuario(AelSolicitacaoExames solicitacaoExames) {
		StringBuffer prontuario = null;
		if(solicitacaoExames != null){
			if(solicitacaoExames.getAtendimento() != null){
				if(solicitacaoExames.getAtendimento().getPaciente().getProntuario() != null){
					prontuario = new StringBuffer(solicitacaoExames.getAtendimento().getPaciente().getProntuario().toString());
				}else{
					if(solicitacaoExames.getAtendimento().getProntuario() != null) {
						prontuario = new StringBuffer(solicitacaoExames.getAtendimento().getProntuario().toString());
					}
				}
			}else{
				if(solicitacaoExames.getAtendimentoDiverso().getAipPaciente() != null && solicitacaoExames.getAtendimentoDiverso().getAipPaciente().getProntuario() != null){
					prontuario = new StringBuffer(solicitacaoExames.getAtendimentoDiverso().getAipPaciente().getProntuario().toString());
				}else{
					if(solicitacaoExames.getAtendimentoDiverso().getProntuario() != null) {
						prontuario = new StringBuffer(solicitacaoExames.getAtendimentoDiverso().getProntuario().toString());
					}
				}
			}
		}
		if(prontuario != null){
			prontuario = new StringBuffer(prontuario.substring(0,prontuario.length()-1)).append('/').append(prontuario.charAt(prontuario.length()-1));
			int zeros = 9 - prontuario.length();
			if(zeros != 0){
				for(int i = 0; i < zeros; i++){
					prontuario.insert(0, "0");
				}
			}
			return prontuario.toString();
		}else{
			return "0";
		}
	}
	
	/**
	 * oradb AELC_LAUDO_PRNT_PAC
	 */
	public String buscarLaudoProntuarioPacienteHist(AelSolicitacaoExamesHist solicitacaoExames) {
		StringBuffer prontuario = null;
		if(solicitacaoExames != null){
			aelSolicitacaoExamesHistDAO.refresh(solicitacaoExames);
			if(solicitacaoExames.getAtendimento() != null){
				if(solicitacaoExames.getAtendimento().getPaciente().getProntuario() != null){
					prontuario = new StringBuffer(solicitacaoExames.getAtendimento().getPaciente().getProntuario().toString());
				}else{
					if(solicitacaoExames.getAtendimento().getProntuario() != null) {
						prontuario = new StringBuffer(solicitacaoExames.getAtendimento().getProntuario().toString());
					}
				}
			}else{
				if(solicitacaoExames.getAtendimentoDiverso().getAipPaciente() != null && solicitacaoExames.getAtendimentoDiverso().getAipPaciente().getProntuario() != null){
					prontuario = new StringBuffer(solicitacaoExames.getAtendimentoDiverso().getAipPaciente().getProntuario().toString());
				}else{
					if(solicitacaoExames.getAtendimentoDiverso().getProntuario() != null) {
						prontuario = new StringBuffer(solicitacaoExames.getAtendimentoDiverso().getProntuario().toString());
					}
				}
			}
		}
		if(prontuario != null){
			prontuario = new StringBuffer(prontuario.substring(0,prontuario.length()-1)).append('/').append(prontuario.charAt(prontuario.length()-1));
			int zeros = 9 - prontuario.length();
			if(zeros != 0){
				for(int i = 0; i < zeros; i++){
					prontuario.insert(0, "0");
				}
			}
			return prontuario.toString();
		}else{
			return "0";
		}
	}
	
	public String buscarLaudoNomePaciente(
			IAelSolicitacaoExames solicitacaoExames) {
		if (solicitacaoExames instanceof AelSolicitacaoExames) {
			return buscarLaudoNomePaciente((AelSolicitacaoExames) solicitacaoExames);
		}
		else {
			return buscarLaudoNomePacienteHist((AelSolicitacaoExamesHist) solicitacaoExames);
		}
	}
	
	/**
	 * oradb AELC_LAUDO_NOME_PAC
	 * 
	 * @HIST RelatorioTicketExamesPacienteON.buscarLaudoNomePacienteHist
	 * @param solicitacaoExames
	 * @return
	 */
	public String buscarLaudoNomePaciente(AelSolicitacaoExames solicitacaoExames) {
		String vNome = "";
		if(solicitacaoExames != null){
				
			solicitacaoExames = aelSolicitacaoExameDAO.obterPorChavePrimaria(solicitacaoExames.getSeq());
			
			if(solicitacaoExames.getAtendimento() != null){
				if(solicitacaoExames.getAtendimento().getPaciente() != null && solicitacaoExames.getAtendimento().getPaciente().getNome() != null){
					return solicitacaoExames.getAtendimento().getPaciente().getNome();
				}
			}else{
				if(solicitacaoExames.getAtendimentoDiverso() != null){
					if(solicitacaoExames.getAtendimentoDiverso().getAipPaciente() != null){
						String vNomePac = solicitacaoExames.getAtendimentoDiverso().getAipPaciente().getNome();
						
						if(solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas() != null
								&& solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero() != null) {
							String vPjqNumero = GPP_+solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero();
							vNome = vPjqNumero+_HIFEN_+vNomePac;
						} else {
							vNome = vNomePac;
						}
					}else if(solicitacaoExames.getAtendimentoDiverso().getNomePaciente() != null){
						String vNomePac = solicitacaoExames.getAtendimentoDiverso().getNomePaciente();
						
						if(solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas() != null
								&& solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero() != null) {
							String vPjqNumero = GPP_+solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero();
							vNome = vPjqNumero+_HIFEN_+vNomePac;
						}else{
							vNome = vNomePac;
						}
					}
					else if(solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas() != null){
						vNome =  GPP_+solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero()+_HIFEN_+solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNome();
					}else if(solicitacaoExames.getAtendimentoDiverso().getAelCadCtrlQualidades() != null){
						vNome =  solicitacaoExames.getAtendimentoDiverso().getAelCadCtrlQualidades().getMaterial();
					}else if(solicitacaoExames.getAtendimentoDiverso().getAelLaboratorioExternos() != null){
						vNome = solicitacaoExames.getAtendimentoDiverso().getAelLaboratorioExternos().getNome();
					}else if(solicitacaoExames.getAtendimentoDiverso().getAelDadosCadaveres() != null){
						vNome = solicitacaoExames.getAtendimentoDiverso().getAelDadosCadaveres().getNome();
					}else if(solicitacaoExames.getAtendimentoDiverso().getAbsCandidatosDoadores() != null){
						vNome = solicitacaoExames.getAtendimentoDiverso().getAbsCandidatosDoadores().getNome();
					}
				}
			}
		}
		if(!vNome.equals("")) {
			vNome = vNome.substring(0,vNome.length() <= 50 ? vNome.length() : 50);
		}
		
		return vNome;
	}
	
	public String buscarLaudoNomeMaeRecemNascido(final IAelSolicitacaoExames solicitacaoExames){
		if (solicitacaoExames instanceof AelSolicitacaoExames) {
			return buscarLaudoNomeMaeRecemNascido((AelSolicitacaoExames) solicitacaoExames);
		}
		else {
			return buscarLaudoNomeMaeRecemNascidoHist((AelSolicitacaoExamesHist) solicitacaoExames);
		}
	}
	
	public String buscarLaudoNomeMaeRecemNascido(final AelSolicitacaoExames solicitacaoExames){
		//não esquecer de refletir alterações neste metodo no histórico buscarLaudoNomeMaeRecemNascido*HIST***
		String vNome = "";
		if(solicitacaoExames != null && solicitacaoExames.getRecemNascido()){
			if(solicitacaoExames.getAtendimento() != null){
				if(solicitacaoExames.getAtendimento().getAtendimentoMae()!=null && solicitacaoExames.getAtendimento().getAtendimentoMae().getPaciente() != null && solicitacaoExames.getAtendimento().getAtendimentoMae().getPaciente().getNome() != null){
					return solicitacaoExames.getAtendimento().getAtendimentoMae().getPaciente().getNome();
				}
			}else{
				if(solicitacaoExames.getAtendimentoDiverso() != null){
					if(solicitacaoExames.getAtendimentoDiverso().getAipPaciente() != null){
						vNome = solicitacaoExames.getAtendimentoDiverso().getAipPaciente().getNome();

					}else if(solicitacaoExames.getAtendimentoDiverso().getNomePaciente() != null){
						vNome = solicitacaoExames.getAtendimentoDiverso().getNomePaciente();
					}
				}
			}
		}
		return vNome;
	}
	
	public String buscarLaudoNomeMaeRecemNascidoHist(final AelSolicitacaoExamesHist solicitacaoExames){
		String vNome = "";
		if(solicitacaoExames != null && solicitacaoExames.getRecemNascido()){
			if(solicitacaoExames.getAtendimento() != null){
				if(solicitacaoExames.getAtendimento().getAtendimentoMae()!=null && solicitacaoExames.getAtendimento().getAtendimentoMae().getPaciente() != null && solicitacaoExames.getAtendimento().getAtendimentoMae().getPaciente().getNome() != null){
					return solicitacaoExames.getAtendimento().getAtendimentoMae().getPaciente().getNome();
				}
			}else{
				if(solicitacaoExames.getAtendimentoDiverso() != null){
					if(solicitacaoExames.getAtendimentoDiverso().getAipPaciente() != null){
						vNome = solicitacaoExames.getAtendimentoDiverso().getAipPaciente().getNome();

					}else if(solicitacaoExames.getAtendimentoDiverso().getNomePaciente() != null){
						vNome = solicitacaoExames.getAtendimentoDiverso().getNomePaciente();
					}
				}
			}
		}
		return vNome;
	}
	
	
	/**
	 * oradb AELC_LAUDO_NOME_PAC
	 * 
	 * @param solicitacaoExames
	 * @return
	 */
	public String buscarLaudoNomePacienteHist(AelSolicitacaoExamesHist solicitacaoExames) {
		String vNome = "";
		
		if(solicitacaoExames != null){
			
			solicitacaoExames = aelSolicitacaoExamesHistDAO.obterPorChavePrimaria(solicitacaoExames.getSeq());
			
			if(solicitacaoExames.getAtendimento() != null){
				if(solicitacaoExames.getAtendimento().getPaciente() != null && solicitacaoExames.getAtendimento().getPaciente().getNome() != null){
					return solicitacaoExames.getAtendimento().getPaciente().getNome();
				}
			}else{
				if(solicitacaoExames.getAtendimentoDiverso() != null){
					if(solicitacaoExames.getAtendimentoDiverso().getAipPaciente() != null){
						String vNomePac = solicitacaoExames.getAtendimentoDiverso().getAipPaciente().getNome();
						
						if(solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas() != null
								&& solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero() != null) {
							String vPjqNumero = GPP_+solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero();
							vNome = vPjqNumero+_HIFEN_+vNomePac;
						}else{
							vNome = vNomePac;
						}
					}else if(solicitacaoExames.getAtendimentoDiverso().getNomePaciente() != null){
						String vNomePac = solicitacaoExames.getAtendimentoDiverso().getNomePaciente();
						
						if(solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas() != null
								&& solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero() != null) {
							String vPjqNumero = GPP_+solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero();
							vNome = vPjqNumero+_HIFEN_+vNomePac;
						}else{
							vNome = vNomePac;
						}
					}
					else if(solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas() != null){
						vNome =  GPP_+solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNumero()+_HIFEN_+solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas().getNome();
					}else if(solicitacaoExames.getAtendimentoDiverso().getAelCadCtrlQualidades() != null){
						vNome =  solicitacaoExames.getAtendimentoDiverso().getAelCadCtrlQualidades().getMaterial();
					}else if(solicitacaoExames.getAtendimentoDiverso().getAelLaboratorioExternos() != null){
						vNome = solicitacaoExames.getAtendimentoDiverso().getAelLaboratorioExternos().getNome();
					}else if(solicitacaoExames.getAtendimentoDiverso().getAelDadosCadaveres() != null){
						vNome = solicitacaoExames.getAtendimentoDiverso().getAelDadosCadaveres().getNome();
					}else if(solicitacaoExames.getAtendimentoDiverso().getAbsCandidatosDoadores() != null){
						vNome = solicitacaoExames.getAtendimentoDiverso().getAbsCandidatosDoadores().getNome();
					}
				}
			}
		}
		if(!vNome.equals("")) {
			vNome = vNome.substring(0,vNome.length() <= 50 ? vNome.length() : 50);
		}
		
		return vNome;
	}
	
	/**
	 * oradb AELC_LAUDO_COD_PAC
	 * 
	 * @param solicitacaoExames
	 * @return
	 */
	public Integer buscarLaudoCodigoPaciente(
			AelSolicitacaoExames solicitacaoExames) {
		if(solicitacaoExames != null){
			if(solicitacaoExames.getAtendimento() != null){
				if(solicitacaoExames.getAtendimento().getPaciente() != null && solicitacaoExames.getAtendimento().getPaciente().getCodigo() != null){
					return solicitacaoExames.getAtendimento().getPaciente().getCodigo();
				}
			}else{
				if(solicitacaoExames.getAtendimentoDiverso() != null){
					if(solicitacaoExames.getAtendimentoDiverso().getAipPaciente() != null){
						return solicitacaoExames.getAtendimentoDiverso().getAipPaciente().getCodigo();
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * oradb AELC_LAUDO_COD_PAC
	 * 
	 * @param solicitacaoExames
	 * @return
	 */
	public Integer buscarLaudoCodigoPacienteHist(
			AelSolicitacaoExamesHist solicitacaoExames) {
		if(solicitacaoExames != null){
			if(solicitacaoExames.getAtendimento() != null){
				if(solicitacaoExames.getAtendimento().getPaciente() != null && solicitacaoExames.getAtendimento().getPaciente().getCodigo() != null){
					return solicitacaoExames.getAtendimento().getPaciente().getCodigo();
				}
			}else{
				if(solicitacaoExames.getAtendimentoDiverso() != null){
					if(solicitacaoExames.getAtendimentoDiverso().getAipPaciente() != null){
						return solicitacaoExames.getAtendimentoDiverso().getAipPaciente().getCodigo();
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * oradb AELC_LAUDO_DTNS_PAC
	 * 
	 * @param solicitacaoExames
	 * @return
	 */
	public Date buscarLaudoDataNascimento(
			AelSolicitacaoExames solicitacaoExames) {
		Date vNascPac = null;
		if(solicitacaoExames != null){
			if(solicitacaoExames.getAtendimento() != null){
				if(solicitacaoExames.getAtendimento().getPaciente() != null && solicitacaoExames.getAtendimento().getPaciente().getDtNascimento() != null){
					return solicitacaoExames.getAtendimento().getPaciente().getDtNascimento();
				}
			}else{
				if(solicitacaoExames.getAtendimentoDiverso() != null){
					if(solicitacaoExames.getAtendimentoDiverso().getAbsCandidatosDoadores() != null){
						vNascPac = solicitacaoExames.getAtendimentoDiverso().getAbsCandidatosDoadores().getDtNascimento();
						return vNascPac;
					}else if(solicitacaoExames.getAtendimentoDiverso().getAipPaciente() != null){
						vNascPac = solicitacaoExames.getAtendimentoDiverso().getAipPaciente().getDtNascimento();
						return vNascPac;
					}else if(solicitacaoExames.getAtendimentoDiverso().getAipPaciente() != null){
						vNascPac = solicitacaoExames.getAtendimentoDiverso().getAipPaciente().getDtNascimento();
						return vNascPac;
					}else if(solicitacaoExames.getAtendimentoDiverso().getDtNascimento() != null){
						vNascPac = solicitacaoExames.getAtendimentoDiverso().getDtNascimento();
						return vNascPac;
					}
				}
			}
		}
		return null;
	}
	
	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	public AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	public AelRecomendacaoExameDAO getAelRecomendacaoExameDAO() {
		return aelRecomendacaoExameDAO;
	}
	
	public AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}
	
	public AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}
	
	public IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}
	
	public IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	public IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	public PesquisarRelatorioTicketExamesPacienteON getPesquisarRelatorioTicketExamesPacienteON() {
		return pesquisarRelatorioTicketExamesPacienteON;
	}

	public void setPesquisarRelatorioTicketExamesPacienteON(
			PesquisarRelatorioTicketExamesPacienteON pesquisarRelatorioTicketExamesPacienteON) {
		this.pesquisarRelatorioTicketExamesPacienteON = pesquisarRelatorioTicketExamesPacienteON;
	}
}
