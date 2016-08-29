package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaCirgRealizadaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaComplFarmacoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagMtvoInternacaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagPrincipalDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagSecundarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEstadoPacienteDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEvolucaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaItemPedidoExameDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaMotivoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaOtrProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaOutraEquipeSumrDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPedidoExameDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPlanoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPrincFarmacoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaRecomendacaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaReinternacaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelSumarioAltaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity"})
@Stateless
public class RelatorioSumarioAltaON extends BaseBusiness  {

	@EJB
	private LaudoProcedimentoSusRN laudoProcedimentoSusRN;
	
	private static final Log LOG = LogFactory.getLog(RelatorioSumarioAltaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmAltaSumarioDAO mpmAltaSumarioDAO;
	
	@Inject
	private MpmAltaComplFarmacoDAO mpmAltaComplFarmacoDAO;
	
	@Inject
	private MpmAltaPrincFarmacoDAO mpmAltaPrincFarmacoDAO;
	
	@Inject
	private MpmAltaPlanoDAO mpmAltaPlanoDAO;
	
	@Inject
	private MpmAltaConsultoriaDAO mpmAltaConsultoriaDAO;
	
	@Inject
	private MpmAltaDiagMtvoInternacaoDAO mpmAltaDiagMtvoInternacaoDAO;
	
	@Inject
	private MpmAltaItemPedidoExameDAO mpmAltaItemPedidoExameDAO;
	
	@Inject
	private MpmAltaEstadoPacienteDAO mpmAltaEstadoPacienteDAO;
	
	@Inject
	private MpmAltaEvolucaoDAO mpmAltaEvolucaoDAO;
	
	@Inject
	private MpmAltaDiagPrincipalDAO mpmAltaDiagPrincipalDAO;
	
	@Inject
	private MpmAltaOtrProcedimentoDAO mpmAltaOtrProcedimentoDAO;
	
	@Inject
	private MpmAltaPedidoExameDAO mpmAltaPedidoExameDAO;
	
	@Inject
	private MpmAltaMotivoDAO mpmAltaMotivoDAO;
	
	@Inject
	private MpmAltaReinternacaoDAO mpmAltaReinternacaoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private MpmAltaDiagSecundarioDAO mpmAltaDiagSecundarioDAO;
	
	@Inject
	private MpmAltaCirgRealizadaDAO mpmAltaCirgRealizadaDAO;
	
	@Inject
	private MpmAltaOutraEquipeSumrDAO mpmAltaOutraEquipeSumrDAO;
	
	@Inject
	private MpmAltaRecomendacaoDAO mpmAltaRecomendacaoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7706363886653454977L;

	@SuppressWarnings("PMD.NPathComplexity")
	public RelSumarioAltaVO criaRelatorioSumarioAlta(MpmAltaSumarioId id) throws ApplicationBusinessException{
		
		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		
		RelSumarioAltaVO relSumario = new RelSumarioAltaVO();
		relSumario.setNomePaciente(altaSumario.getNome());
		relSumario.setIdade(criaIdadeExtenso(altaSumario.getIdadeAnos(),altaSumario.getIdadeMeses(),altaSumario.getIdadeDias()));
		relSumario.setSexo(altaSumario.getSexo().getDescricao());
		relSumario.setDataInternacao(altaSumario.getDthrAtendimento());
		if(altaSumario.getDthrElaboracaoAlta() != null){
			relSumario.setDataAlta(altaSumario.getDthrElaboracaoAlta());
		}else{
			relSumario.setDataAlta(new Date());
		}
		relSumario.setProntuario(CoreUtil.formataProntuarioRelatorio(altaSumario.getProntuario()));
		if(altaSumario.getLeito()!=null){
			relSumario.setLeito(altaSumario.getLeito().getLeitoID());
		}
		relSumario.setPermanencia(altaSumario.getDiasPermanencia() + " dias");
		relSumario.setConvenio(altaSumario.getDescConvenio());
		relSumario.setEquipeResponsavel(altaSumario.getDescEquipeResponsavel());
		relSumario.setServicoResponsavel(altaSumario.getDescServicoResponsavel());
		relSumario.setIndetificador(id.toString());

		Integer matriculaValida = altaSumario.getServidorValida() != null ? altaSumario.getServidorValida().getId().getMatricula() : null;
		Short 	vinCodigoValida = altaSumario.getServidorValida() != null ? altaSumario.getServidorValida().getId().getVinCodigo() : null;
		relSumario.setNomeSiglaRegistro(buscaNomeSiglaRegistro(matriculaValida, vinCodigoValida));

		relSumario.setOutrasEquipes(buscaOutrasEquipes(id));
		relSumario.setMotivosInternacao(buscaAltaDiagMtvoInternacoes(id));  
		relSumario.setDignosticoPrincipalAlta(buscaAltaDiagPrincipal(id));  
		relSumario.setDignosticosSecundarios(buscaAltaDiagSecundarios(id));
		relSumario.setCirurgiasRealizadas(buscaAltaCirgRealizadas(id));
		relSumario.setOutrosProcedimentos(buscaOutrosProcedimentos(id));
		relSumario.setConsultorias(buscaAltaConsultorias(id));
		relSumario.setPrincipaisFarmacos(buscaPrincFarmacos(id));   
		relSumario.setComplementoFarmacos(buscaComplFarmacos(id));
		relSumario.setEvolucao(buscaEvolucoes(id));   		
		relSumario.setMotivoAlta(buscaAltaMotivos(id)); 
		relSumario.setPlanoPosAlta(buscaAltaPlano(id));
		relSumario.setRecomendacoesAlta(buscaAltaRecomendacoes(id));
		relSumario.setConsultas(buscaAltaPedidoExame(id));
		relSumario.setReInternacao(buscaAltaReinternacao(id));
		relSumario.setExamesSolicitados(buscaAltaItemPedidoExame(id));
		relSumario.setEstadoPaciente(buscaAltaEstadoPaciente(id));
		
		Integer indice = 1;
		
		if ((relSumario.getMotivosInternacao() != null && !relSumario.getMotivosInternacao().isEmpty())
				|| relSumario.getDignosticoPrincipalAlta() != null) {
			
			relSumario.setIndiceDiagnostico(++indice + ".");
		}
		
		if ((relSumario.getCirurgiasRealizadas()!=null && !relSumario.getCirurgiasRealizadas().isEmpty())
			||(relSumario.getOutrosProcedimentos()!=null && !relSumario.getOutrosProcedimentos().isEmpty())
			||(relSumario.getConsultorias()!=null && !relSumario.getConsultorias().isEmpty())
			||(relSumario.getPrincipaisFarmacos()!=null && !relSumario.getPrincipaisFarmacos().isEmpty())
			||(relSumario.getComplementoFarmacos()!=null && !relSumario.getComplementoFarmacos().isEmpty())){
			
			relSumario.setIndiceProcedTerapeutico(++indice + ".");
		}
		
		if (relSumario.getEvolucao() != null){
			relSumario.setIndiceEvolucao(++indice + ".");
		}
		
		if (relSumario.getMotivoAlta()!=null || relSumario.getPlanoPosAlta()!=null 
				|| (relSumario.getRecomendacoesAlta()!= null && !relSumario.getRecomendacoesAlta().isEmpty())){
			
			relSumario.setIndicePlanoPosAlta(++indice + ".");
		}
		
		if (relSumario.getConsultas()!=null || relSumario.getReInternacao()!=null 
				|| (relSumario.getExamesSolicitados() != null && !relSumario.getExamesSolicitados().isEmpty())){
			
			relSumario.setIndiceSeguimentoAtendimento(++indice + ".");
		}
		
		if (relSumario.getEstadoPaciente() != null){
			relSumario.setIndiceEstadoAlta(++indice + ".");
		}
		
		
		if (altaSumario.getLeito() != null) {
			relSumario.setLocalRodape("Leito: " + altaSumario.getLeito().getLeitoID());
		} else if (altaSumario.getQuarto() != null) {
			relSumario.setLocalRodape("Quarto: " + altaSumario.getQuarto().getDescricao());
		} else if (altaSumario.getUnidadeFuncional() != null) {
			StringBuffer unidade = null;
			if (altaSumario.getUnidadeFuncional().getSigla() != null) {
				unidade = new StringBuffer(altaSumario.getUnidadeFuncional().getSigla());
			} else {
				unidade = new StringBuffer(altaSumario.getUnidadeFuncional().getAndar().toString());
				if (altaSumario.getUnidadeFuncional().getIndAla() != null) {
					unidade.append(altaSumario.getUnidadeFuncional().getIndAla());
				}
			}
			relSumario.setLocalRodape(unidade.insert(0, "Unidade: ").toString());
		}
		
		return relSumario;
	}
	
	private String buscaNomeSiglaRegistro(Integer matricula, Short vinculo) throws ApplicationBusinessException{
		BuscaConselhoProfissionalServidorVO vo = laudoProcedimentoSusRN.buscaConselhoProfissionalServidorVO(matricula, vinculo, Boolean.FALSE);
		if(StringUtils.isNotBlank(vo.getSiglaConselho()) && StringUtils.isNotBlank(vo.getNome())){
			return substituiNullPorEspaco(vo.getNome(),true) + 
				   substituiNullPorEspaco(vo.getSiglaConselho(),true)+ 
				   substituiNullPorEspaco(vo.getNumeroRegistroConselho(),false);
		}else{
			if(matricula != null && vinculo != null){
				return getRegistroColaboradorFacade().buscarServidor(vinculo, matricula).getPessoaFisica().getNome();
			}else{
				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
				
				return servidorLogado.getPessoaFisica().getNome();
			}
		}
	}	
	
	private List<LinhaReportVO> buscaOutrasEquipes(final MpmAltaSumarioId id){
		List<LinhaReportVO> lista = this.getMpmAltaOutraEquipeSumrDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		List<LinhaReportVO> novaLista = new ArrayList<LinhaReportVO>();
		for (LinhaReportVO linha : lista){
			linha.setTexto1(WordUtils.capitalizeFully(linha.getTexto1()).replaceAll(" Da ", " da ").replaceAll(" De ", " de ").replaceAll(" Do ", " do "));
			novaLista.add(linha);
		}
		return novaLista;
	}
	
	private List<LinhaReportVO> buscaAltaDiagMtvoInternacoes(final MpmAltaSumarioId id){
		return this.getMpmAltaDiagMtvoInternacaoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteVO();
	}
	
	private String buscaAltaDiagPrincipal(final MpmAltaSumarioId id){
		return this.getMpmAltaDiagPrincipalDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteTexto();
	}	

	private List<LinhaReportVO> buscaAltaDiagSecundarios(final MpmAltaSumarioId id){
		return this.getMpmAltaDiagSecundarioDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}	
	
	private List<LinhaReportVO> buscaAltaCirgRealizadas(final MpmAltaSumarioId id){
		return this.getMpmAltaCirgRealizadaDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteVO();
	}		

	private List<LinhaReportVO> buscaOutrosProcedimentos(final MpmAltaSumarioId id){
		return this.getMpmAltaOtrProcedimentoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteVO();
	}
	
	private List<LinhaReportVO> buscaAltaConsultorias(final MpmAltaSumarioId id){
		return this.getMpmAltaConsultoriaDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteVO();
	}		
	
	private List<LinhaReportVO> buscaPrincFarmacos(final MpmAltaSumarioId id){
		return this.getMpmAltaPrincFarmacoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteVO();
	}		
	
	private List<LinhaReportVO> buscaComplFarmacos(final MpmAltaSumarioId id){
		return this.getMpmAltaComplFarmacoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteVO();
	}		

	private String buscaEvolucoes(final MpmAltaSumarioId id){
		return this.getMpmAltaEvolucaoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteTexto();
	}		
	
	private String buscaAltaMotivos(final MpmAltaSumarioId id){
		return this.getMpmAltaMotivoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteTexto();
	}		

	private String buscaAltaPlano(final MpmAltaSumarioId id){
		return this.getMpmAltaPlanoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		//return geraDadosTesteTexto();
	}		

	private List<LinhaReportVO> buscaAltaRecomendacoes(final MpmAltaSumarioId id){
		return this.getMpmAltaRecomendacaoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
//		return geraDadosTesteVO();
	}
	
	private String buscaAltaPedidoExame(final MpmAltaSumarioId id){
		return this.getMpmAltaPedidoExameDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
//		return geraDadosTesteTexto();
	}		
	
	private String buscaAltaReinternacao(final MpmAltaSumarioId id){ 
		return this.getMpmAltaReinternacaoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
//		return geraDadosTesteTexto();
	}		
	
	private List<LinhaReportVO> buscaAltaItemPedidoExame(final MpmAltaSumarioId id){ 
		return this.getMpmAltaItemPedidoExameDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
//		return geraDadosTesteVO();
	}
	
	private String buscaAltaEstadoPaciente(final MpmAltaSumarioId id){
		return this.getMpmAltaEstadoPacienteDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
//		return geraDadosTesteTexto();
	}		

	private String criaIdadeExtenso(Short anos, Integer meses, Integer dias){		
		StringBuffer idadeExt = new StringBuffer();
		if (anos!=null && anos > 1) {
			idadeExt.append(anos).append(" anos ");
		}else if (anos!=null && anos == 1) {
			idadeExt.append(anos).append(" ano ");
		}
		if (meses!=null && meses > 1) {
			idadeExt.append(meses).append(" meses ");
		}else if (meses!=null && meses == 1) {
			idadeExt.append(meses).append(" mês ");
		}
		if (anos==null || anos == 0){ 
			if (dias!=null && dias > 1) {
				idadeExt.append(dias).append(" dias ");
			}else if (dias!=null && dias == 1) {
				idadeExt.append(dias).append(" dia ");
			}
		}		
		return idadeExt.toString();
	}

	private String substituiNullPorEspaco(String str, boolean addEspaco){
		if (str==null){
			return "";
		}else{
			if (addEspaco){
				return str+" ";
			}else{
				return str;
			}	
		}
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	protected MpmAltaCirgRealizadaDAO getMpmAltaCirgRealizadaDAO() {
		return mpmAltaCirgRealizadaDAO;
	}
	
	protected MpmAltaComplFarmacoDAO getMpmAltaComplFarmacoDAO() {
		return mpmAltaComplFarmacoDAO;
	}
	
	protected MpmAltaConsultoriaDAO getMpmAltaConsultoriaDAO() {
		return mpmAltaConsultoriaDAO;
	}
	
	protected MpmAltaDiagMtvoInternacaoDAO getMpmAltaDiagMtvoInternacaoDAO() {
		return mpmAltaDiagMtvoInternacaoDAO;
	}
	
	protected MpmAltaDiagPrincipalDAO getMpmAltaDiagPrincipalDAO() {
		return mpmAltaDiagPrincipalDAO;
	}
	
	protected MpmAltaDiagSecundarioDAO getMpmAltaDiagSecundarioDAO() {
		return mpmAltaDiagSecundarioDAO;
	}
	
	protected MpmAltaEstadoPacienteDAO getMpmAltaEstadoPacienteDAO() {
		return mpmAltaEstadoPacienteDAO;
	}
	
	protected MpmAltaEvolucaoDAO getMpmAltaEvolucaoDAO() {
		return mpmAltaEvolucaoDAO;
	}
	
	protected MpmAltaItemPedidoExameDAO getMpmAltaItemPedidoExameDAO() {
		return mpmAltaItemPedidoExameDAO;
	}
	
	protected MpmAltaMotivoDAO getMpmAltaMotivoDAO() {
		return mpmAltaMotivoDAO;
	}
	
	protected MpmAltaOtrProcedimentoDAO getMpmAltaOtrProcedimentoDAO() {
		return mpmAltaOtrProcedimentoDAO;
	}
	
	protected MpmAltaOutraEquipeSumrDAO getMpmAltaOutraEquipeSumrDAO() {
		return mpmAltaOutraEquipeSumrDAO;
	}
	
	protected MpmAltaPedidoExameDAO getMpmAltaPedidoExameDAO() {
		return mpmAltaPedidoExameDAO;
	}
	
	protected MpmAltaPlanoDAO getMpmAltaPlanoDAO() {
		return mpmAltaPlanoDAO;
	}
	
	protected MpmAltaPrincFarmacoDAO getMpmAltaPrincFarmacoDAO() {
		return mpmAltaPrincFarmacoDAO;
	}
	
	protected MpmAltaRecomendacaoDAO getMpmAltaRecomendacaoDAO() {
		return mpmAltaRecomendacaoDAO;
	}
	
	protected MpmAltaReinternacaoDAO getMpmAltaReinternacaoDAO() {
		return mpmAltaReinternacaoDAO;
	}
	
	protected MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}