package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacientesEntrevistarVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioPacientesEntrevistarON extends BaseBusiness {

	private static final String _______ = "_______";

	private static final Log LOG = LogFactory.getLog(RelatorioPacientesEntrevistarON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;


	@EJB
	private FichaPreOperatoriaRN fichaPreOperatoriaRN;

	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	private static final long serialVersionUID = 4205311708120728641L;

	private enum RelatorioPacientesEntrevistarONExceptonCode implements BusinessExceptionCode{
		MSG_INFORME_UNF,MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA;
	}
	
	public List<ProfDescricaoCirurgicaVO> listarAnestesistas(String strPesquisa,AghUnidadesFuncionais unf) throws ApplicationBusinessException {

		return this.pesquisarAnestesistas(strPesquisa, unf);
	}

	public Long listarAnestesistasCount(String strPesquisa, AghUnidadesFuncionais unf) throws ApplicationBusinessException {

		List<ProfDescricaoCirurgicaVO> listAnestesistas = pesquisarAnestesistas(strPesquisa, unf);

		if(listAnestesistas!=null && !listAnestesistas.isEmpty()){
			return Long.valueOf(listAnestesistas.size());
		}else{
			return Long.valueOf(0); 
		}
	}

	/**
	 * SugestionBox de Anestesistas
	 * @param strPesquisa
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private List<ProfDescricaoCirurgicaVO> pesquisarAnestesistas(
			String strPesquisa,AghUnidadesFuncionais unf ) throws ApplicationBusinessException {
		Integer matricula = null;
		String nome = null;
		
		if(unf == null){
			throw new ApplicationBusinessException(RelatorioPacientesEntrevistarONExceptonCode.MSG_INFORME_UNF);
		}
		
		if(strPesquisa!=null && CoreUtil.isNumeroInteger(strPesquisa)){
			matricula = Integer.valueOf(strPesquisa);
		}else{
			nome = strPesquisa;
		}
		
		ArrayList<DominioFuncaoProfissional> listaFuncaoProf = new ArrayList<DominioFuncaoProfissional>();
		listaFuncaoProf.add(DominioFuncaoProfissional.ANR);
		listaFuncaoProf.add(DominioFuncaoProfissional.ANC);
		listaFuncaoProf.add(DominioFuncaoProfissional.ANP);

		String listaSiglaConselho = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_LISTA_CONSELHOS_PROF_MBC); //'CRO','CREMERS'

		List<String> listaSiglas = Arrays.asList(listaSiglaConselho.split(","));

		List<ProfDescricaoCirurgicaVO> listAnestesistas = getMbcProfAtuaUnidCirgsDAO().pesquisarProfAtuaUnidCirgConselhoPorServidorUnfSeq(matricula, null, unf.getSeq()
				, listaSiglas, DominioSituacao.A, listaFuncaoProf,nome);
		return listAnestesistas;
	}


	/**
	 * Pesquisa do relatório de pacientes a entrevistar
	 * @param seqUnidadeCirurgica
	 * @param seqUnidadeInternacao
	 * @param dataCirurgia
	 * @param pacienteInternado
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public List<RelatorioPacientesEntrevistarVO> pesquisarRelatorioPacientesEntrevistar(Short seqUnidadeCirurgica, Date dataCirurgia, ProfDescricaoCirurgicaVO anestesistaVO) throws ApplicationBusinessException {


		RapServidores servidorAnest = getRegistroColaboradorFacade().obterServidor(new RapServidores(new RapServidoresId(anestesistaVO.getSerMatricula(),anestesistaVO.getSerVinCodigo())));

		List<MbcProfCirurgias> listProfCir = getMbcProfAtuaUnidCirgsDAO().pesquisarRelatorioPacientesEntrevistar(servidorAnest, dataCirurgia, seqUnidadeCirurgica);

		List<RelatorioPacientesEntrevistarVO> listVo = new ArrayList<RelatorioPacientesEntrevistarVO>();
		RelatorioPacientesEntrevistarVO vo = null;

		for (MbcProfCirurgias profCir : listProfCir) {

			vo = new RelatorioPacientesEntrevistarVO();
			vo.setIndfuncao(profCir.getFuncaoProfissional().getCodigo());
			vo.setAnestesista(profCir.getServidor().getPessoaFisica().getNome());
			vo.setSala(profCir.getCirurgia().getSciSeqp().toString());
			vo.setDataHrInicio(DateUtil.obterDataFormatada(profCir.getCirurgia().getDataPrevisaoInicio(), "HH:mm"));
			vo.setDataHrFim(DateUtil.obterDataFormatada(profCir.getCirurgia().getDataPrevisaoFim(), "HH:mm"));
			vo.setAgenda(profCir.getCirurgia().getNumeroAgenda().toString());
			vo.setLocal(pesquisaLocal(profCir.getCirurgia().getAtendimento()));
			vo.setPaciente(profCir.getCirurgia().getPaciente().getNome());
			vo.setIdade(this.mbccIdadeExt(profCir.getCirurgia().getPaciente().getDtNascimento(), profCir.getCirurgia().getData()));  //translate(nvl(mbcc_idade_ext(PAC.DT_NASCIMENTO),' '),'AM','    ') DT_NASCIMENTO 
			vo.setSexo(profCir.getCirurgia().getPaciente().getSexo().toString());
			if(profCir.getCirurgia().getPaciente()!=null && profCir.getCirurgia().getPaciente().getProntuario()!=null){
				/** Máscara prontuário */
				String prontAux = profCir.getCirurgia().getPaciente().getProntuario().toString();
				vo.setProntuario(prontAux.substring(0,prontAux.length() - 1)+"/"+prontAux.charAt(prontAux.length() - 1));
			}
			/**
			 * Anestesista Professor
			 */
			MbcProfCirurgias anestProfessor = this.getMbcProfCirurgiasDAO().obterAnestesistaProfessorEscalaCirurgicaPorCrgSeq(profCir.getCirurgia().getSeq(), seqUnidadeCirurgica);
			vo.setAnestProfessor(anestProfessor!=null?anestProfessor.getServidor().getPessoaFisica().getNome():"");
			/**
			 * Anestesista Contratado
			 */
			vo.setListAnestesistaContratado(preencherAnestesistaContratado(profCir.getCirurgia().getSeq()));
			/**
			 * Cirurgião
			 */
			vo.setCirurgiao(preencherNomeCirurgiao(profCir.getCirurgia().getSeq()));
			/**
			 * Procedimentos
			 */
			vo.setListProcedimentos(processarListaProcedimentos(profCir.getCirurgia().getSeq()));
			/**
			 * Sugestão Anestesia
			 */
			vo.setSugestao(preencherAnestesiaDescricao(profCir.getCirurgia().getSeq()));
			listVo.add(vo);
		}
		
		if(listVo.isEmpty()){
			throw new ApplicationBusinessException(RelatorioPacientesEntrevistarONExceptonCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		return listVo;
	}


	/**
	 * ORADB FUNCTION MBCC_LOCAL_PAC( P_ATD_SEQ IN NUMBER )
	 * @param atendimento
	 * @return
	 */
	public String pesquisaLocal(AghAtendimentos atendimento) {

		if(atendimento!=null){

			if(atendimento.getOrigem().equals(DominioOrigemAtendimento.C)){
				return _______;
			}else if(atendimento.getOrigem().equals(DominioOrigemAtendimento.A)){
				Boolean emergencia = this.getAghuFacade().verificarCaracteristicaUnidadeFuncional(atendimento.getUnidadeFuncional().getSeq(),
						ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA);
				if(emergencia){
					return "U: EMG";
				}else{
					return "U: AMB";
				}
			}else if(atendimento.getOrigem().equals(DominioOrigemAtendimento.X)){
				return "U: EXT";
			}else if(atendimento.getOrigem().equals(DominioOrigemAtendimento.D)){
				return "U: DOA";
			}else if(atendimento.getOrigem().equals(DominioOrigemAtendimento.U)){

				if(atendimento.getUnidadeFuncional().getSigla()!=null){
					return "U: "+atendimento.getUnidadeFuncional().getSigla().substring(1,5);
				}else{
					return _______;
				}

			}else if (atendimento.getLeito()!=null && atendimento.getLeito().getLeitoID()!=null){
				return "L:"+atendimento.getLeito().getLeitoID();
			}else if(atendimento.getQuarto()!=null && atendimento.getQuarto().getNumero()!=null){
				return "Q:"+atendimento.getQuarto().getNumero();
			}else if(atendimento.getUnidadeFuncional()!=null){

				if(atendimento.getUnidadeFuncional().getAndar()==null ){
					return _______;
				}else{
					return "U:"+atendimento.getUnidadeFuncional().getAndar()+" "+atendimento.getUnidadeFuncional().getIndAla()!=null?atendimento.getUnidadeFuncional().getIndAla().getDescricao():"";
				}

			}else{
				return _______;
			}

		}else{

			return _______;

		}

	}


	protected String preencherAnestesiaDescricao(Integer crgSeq) {
		List<MbcAnestesiaCirurgias> listMbcAnestesiaCirurgias = getMbcAnestesiaCirurgiasDAO()
		.listarAnestesiaCirurgiaTipoAnestesiaPorCrgSeq(crgSeq);
		String retorno = "";
		for (MbcAnestesiaCirurgias anestesiaCirurgias : listMbcAnestesiaCirurgias) {
			if(retorno.isEmpty()){
				retorno = retorno.concat(anestesiaCirurgias.getMbcTipoAnestesias().getDescricao());
			}else{
				retorno = retorno.concat(", ").concat(anestesiaCirurgias.getMbcTipoAnestesias().getDescricao());
			}
		}
		return retorno;
	}

	private List<LinhaReportVO> processarListaProcedimentos(Integer crgSeq) {

		List<LinhaReportVO> listaProcedimentos= new ArrayList<LinhaReportVO>();

		List<MbcProcEspPorCirurgias> listProcEspPorCirurgias = getMbcProcEspPorCirurgiasDAO().getPesquisarProcedimentoCirurgicoEscalaCirurgica(crgSeq, DominioIndRespProc.AGND);
		for (MbcProcEspPorCirurgias procEspPorCirurgias : listProcEspPorCirurgias) {

			LinhaReportVO linhaReportProcedimento = new LinhaReportVO();			
			linhaReportProcedimento.setTexto1(procEspPorCirurgias.getProcedimentoCirurgico().getDescricao());

			listaProcedimentos.add(linhaReportProcedimento);			
		}	
		if(listaProcedimentos.isEmpty()){
			return null;
		}
		return listaProcedimentos;		
	}


	private List<LinhaReportVO> preencherAnestesistaContratado(Integer crgSeq) {
		List<LinhaReportVO> listAnestContratados = null;
		DominioFuncaoProfissional[] listFuncaoProfissional = new DominioFuncaoProfissional[] { DominioFuncaoProfissional.ANC};
		List<MbcProfCirurgias> listProfCirurgias = getMbcProfCirurgiasDAO().listarMbcProfCirurgiasPorCrgSeqFuncaoProfissionalOrder(crgSeq, listFuncaoProfissional,null);

		if(listProfCirurgias!=null && !listProfCirurgias.isEmpty()){

			listAnestContratados = new ArrayList<LinhaReportVO>();
			for (MbcProfCirurgias anestCont :listProfCirurgias){
				LinhaReportVO anestContratVO = new LinhaReportVO();
				anestContratVO.setTexto1(anestCont.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
				listAnestContratados.add(anestContratVO);
			}
		}

		return listAnestContratados;
	}



	public String preencherNomeCirurgiao(Integer crgSeq) {
		String retorno = "";
		DominioFuncaoProfissional[] listFuncaoProfissional = new DominioFuncaoProfissional[] { DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MPF};
		List<MbcProfCirurgias> listProfCirurgias = getMbcProfCirurgiasDAO().listarMbcProfCirurgiasPorCrgSeqFuncaoProfissionalOrder(crgSeq, listFuncaoProfissional,true);
		for (MbcProfCirurgias profCirurgias : listProfCirurgias) {
			if(!retorno.equals("")){
				retorno = retorno.concat("\n");
			}
			if(profCirurgias.getServidorPuc().getPessoaFisica().getNomeUsual() != null){
				retorno = retorno.concat(profCirurgias.getServidorPuc().getPessoaFisica().getNomeUsual());
			}else{
				retorno = retorno.concat(getAmbulatorioFacade().formataString(profCirurgias.getServidorPuc().getPessoaFisica().getNome(), 16));
			}
		}
		return retorno;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return iAmbulatorioFacade;
	}

	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}

	public FichaPreOperatoriaRN getFichaPreOperatoriaRN(){
		return fichaPreOperatoriaRN;
	}

	public String mbccIdadeExt(Date dtNascimento, Date dataCrg){
		String idadeFormat = null;
		if (dtNascimento != null) {
			Calendar dataNascimento = new GregorianCalendar();
			dataNascimento.setTime(dtNascimento);
			Calendar dataCalendario = new GregorianCalendar();
			Integer idadeNum = dataCalendario.get(Calendar.YEAR)
			- dataNascimento.get(Calendar.YEAR);

			if (dataCalendario.get(Calendar.MONTH) < dataNascimento
					.get(Calendar.MONTH)) {
				idadeNum--;
			} else if (dataCalendario.get(Calendar.MONTH) == dataNascimento
					.get(Calendar.MONTH)
					&& dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento
					.get(Calendar.DAY_OF_MONTH)) {
				idadeNum--;
			}

			idadeFormat = idadeNum.toString() ;
		}

		return idadeFormat;
	}

	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO() {
		return mbcProfAtuaUnidCirgsDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}


	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
}
