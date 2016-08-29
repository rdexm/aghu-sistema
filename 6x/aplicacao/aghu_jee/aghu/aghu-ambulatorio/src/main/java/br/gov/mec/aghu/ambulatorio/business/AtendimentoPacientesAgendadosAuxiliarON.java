package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamAltasSumarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamLaudoAihDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRelatorioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamSolicProcedimentoDAO;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.ambulatorio.vo.MamRelatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioTratamentoFisiatricoVO;
import br.gov.mec.aghu.ambulatorio.vo.VerificaItemSolicitacaoExamesVO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghCidDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioIndPendenteLaudoAih;
import br.gov.mec.aghu.exames.dao.AelExQuestionarioOrigensDAO;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelRespostaQuestaoDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.vo.ItemSolicitacaoExameAtendimentoVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.model.MamRelatorio;
import br.gov.mec.aghu.model.MamSolicProcedimento;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.vo.AtendimentoPrescricaoPacienteVO;
import br.gov.mec.aghu.paciente.vo.DescricaoCodigoComplementoCidVO;
import br.gov.mec.aghu.paciente.vo.QtdSessoesTratamentoVO;
import br.gov.mec.aghu.parametrosistema.business.IParametroSistemaFacade;
import br.gov.mec.aghu.parametrosistema.vo.AghParametroVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptItemPrcrModalidadeDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptPrescricaoPacienteDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ImagemModalidadeOrientacaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


@Stateless
public class AtendimentoPacientesAgendadosAuxiliarON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AtendimentoPacientesAgendadosAuxiliarON.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	private static final long serialVersionUID = 6282720270279716751L;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MamRelatorioDAO mamRelatorioDAO;
	
	@Inject
	private MamAltasSumarioDAO mamAltasSumarioDAO;
	
	@Inject
	private MamLaudoAihDAO mamLaudoAihDAO;
		
	@Inject
	private MamSolicProcedimentoDAO mamSolicProcedimentoDAO;

	@EJB
	private IParametroSistemaFacade parametroSistemaFacade;
		
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	
	@Inject
	private AghCidDAO aghCidDAO;
	
	@Inject
	private MptPrescricaoPacienteDAO mptPrescricaoPacienteDAO;
	
	@Inject
	private MptItemPrcrModalidadeDAO mptItemPrcrModalidadeDAO;

	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject
	private AelExQuestionarioOrigensDAO aelExQuestionarioOrigensDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelExamesDAO aelExamesDAO;
	
	@Inject
	private AelRespostaQuestaoDAO aelRespostaQuestaoDAO;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO; 
	
	@EJB
	private ImprimirGuiaAtendimentoUnimedRN imprimirGuiaAtendimentoUnimedRN;
	
	/**
	 * Verifica se o paciente tem documentos a serem impressos.
	 * 
	 * ORADB: Package MAMK_GENERICA.MAMC_VER_DOC_IMP
	 * 
	 * @param consulta
	 *  
	*/
	public Boolean verificarImpressaoDocumentosPaciente(AacConsultas consulta, Boolean vTemImp, Boolean verificarProcesso) throws ApplicationBusinessException {

		vTemImp = verificarRelatorioMedico(consulta, vTemImp, verificarProcesso);

		//Alta Sumarios
		vTemImp = verificarAltaSumarios(consulta, vTemImp, verificarProcesso);

		//Laudo Aih
		vTemImp = verificarLaudoAih(consulta, vTemImp, verificarProcesso);

		return vTemImp;
	}
	
	/**
	 * Obtem a lista de documentos a serem impressos
	 * @param conNumero
	 * 
	 */
	public List<DocumentosPacienteVO> obterListaDocumentosPaciente(List<DocumentosPacienteVO> listaDocumentos, Integer conNumero, Integer gradeSeq,String vSituacaoCancelado,String vSituacaoPendente, Short paramSolicitacaoExames) throws ApplicationBusinessException {
		
		obterListaRelatorioMedico(conNumero, listaDocumentos);
		
		obterListaAltaSumarios(conNumero, listaDocumentos);
		
		obterListaLaudoAih(conNumero, listaDocumentos,vSituacaoCancelado, vSituacaoPendente,paramSolicitacaoExames);
		
		adicionarGuiaAtendimentoUnimed(conNumero, listaDocumentos);
		
		return listaDocumentos;
	}
	
	private void adicionarGuiaAtendimentoUnimed(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos) throws ApplicationBusinessException {
		if (imprimirGuiaAtendimentoUnimedRN.verificarConvenioUnimed(conNumero, false)) {		
			DocumentosPacienteVO documento = new DocumentosPacienteVO();
			documento.setDescricao("Guia Unimed");
			documento.setGuiaAtendimentoUnimed(Boolean.TRUE);
			listaDocumentos.add(documento);	
		}
	}
	
	/**
	 * Verifica se o usuário pode ou não validar um determinado processo
	 * 
	 * ORADB: Package MAMK_PERFIL.MAMC_VALIDA_PROCESSO
	 * 
	 * @param pSerVinCodigo, pSerMatricula, pSeqProcesso
	 * 
	 */
	public Boolean validarProcesso(Short pSerVinCodigo, Integer pSerMatricula, Short pSeqProcesso) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		String login = null;
		if (pSerVinCodigo != null && pSerMatricula != null) {
			RapServidores servidor = getRegistroColaboradorFacade().obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(pSerMatricula, pSerVinCodigo);
			if (servidor.getUsuario() != null) {
				login = servidor.getUsuario().toUpperCase();
			}
		}
		if (login == null) {
			login = servidorLogado.getUsuario();
		}

		if (getCascaFacade().validarPermissaoPorServidorESeqProcesso(login, pSeqProcesso)) {
			return true;
		}

		return false;
	}
	
//	private String validarObjetoNulo(Object object) {
//		if (object == null) {
//			return "";
//		} else {
//			return object.toString();
//		}
//	}

	/**
	 * #43029 - ON2
	 * @param consulta
	 * @param vTemImp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Boolean verificarRelatorioMedico(AacConsultas consulta, Boolean vTemImp, Boolean verificarProcesso) throws ApplicationBusinessException{
		
		if(consulta != null && consulta.getNumero() != null){

			List<MamRelatorioVO> listaMamRelatorioVO = mamRelatorioDAO.obterListaMamRelatorioVOPorConNumero(consulta.getNumero());
			
			BigDecimal valorParametro = this.parametroFacade.obterAghParametroPorNome(AghuParametrosEnum.P_SEQ_PROC_RELATORIO.toString()).getVlrNumerico();
			
			for (MamRelatorioVO mamRelatorioVO : listaMamRelatorioVO) {
				if(mamRelatorioVO.getIndPendente().equals("V") 
						|| (mamRelatorioVO.getIndPendente().equals("P") && (!verificarProcesso || validarProcesso(null, null, valorParametro.shortValue())))){
					vTemImp = Boolean.TRUE;
				}
			}
		}
		return vTemImp;
	}
	
	//Alta Ambulatorial
	private Boolean verificarAltaSumarios(AacConsultas conNumero, Boolean vTemImp, Boolean verificarProcesso) throws ApplicationBusinessException{
		
		List<MamAltaSumario> cAls = mamAltasSumarioDAO.verificarExisteAltaSumario(conNumero.getNumero());
		for (MamAltaSumario mamAltaSumario : cAls) {
			if(mamAltaSumario.getPendente().equals(DominioIndPendenteDiagnosticos.V) ||
			  (mamAltaSumario.getPendente().equals(DominioIndPendenteDiagnosticos.P) &&
			  (!verificarProcesso || this.validarProcesso(null, null, retornarCodigoProcessoAlta())))){
				vTemImp = Boolean.TRUE;
			}
		}
		return vTemImp;
	}
	
	public Boolean verificarLaudoAih(AacConsultas conNumero, Boolean vTemImp, Boolean verificarProcesso) throws ApplicationBusinessException{
	//Laudo	
		List<MamLaudoAih> mamLaudoAih = mamLaudoAihDAO.verificarLaudoAihParaImpressao(conNumero.getNumero()); 
		for (MamLaudoAih mamLaudoAih2 : mamLaudoAih) {
			if(mamLaudoAih2.getIndPendente().equals(DominioIndPendenteLaudoAih.V) || 
			  (mamLaudoAih2.getIndPendente().equals(DominioIndPendenteLaudoAih.P) && 
					  (!verificarProcesso || this.validarProcesso(null, null, this.retornarCodigoProcessoLaudo()))) ){				
				vTemImp = Boolean.TRUE;	
			}
		}
		return vTemImp;
	}
		
	
	
	/**
	 * Atualiza MamRelatorio indicando que foi impresso.
	 * @param seq
	 */
	public void atualizarIndImpressoRelatorioMedico(Long seq){
		MamRelatorio mamRelatorio = mamRelatorioDAO.obterPorChavePrimaria(seq);
		mamRelatorio.setIndImpresso("S");
		mamRelatorioDAO.atualizar(mamRelatorio);
	}


	/**
	 * #43029 ON6 - CHAMADA PROCEDURE P1
	 * @param conNumero
	 * @param listaDocumentos
	 * @throws ApplicationBusinessException
	 */
	private void obterListaRelatorioMedico(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos) throws ApplicationBusinessException{
		
		List<MamRelatorioVO> listaMamRelatorioVO = mamRelatorioDAO.obterCurRelPorConNumero(conNumero);
		Integer vQtdeRel = 0;
		
		BigDecimal valorParametro = parametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_SEQ_PROC_RELATORIO.toString());
		
		for (MamRelatorioVO mamRelatorioVO : listaMamRelatorioVO) {
			
			vQtdeRel = vQtdeRel + 1;
			String retorno = pPopulaTelaRelatorio(mamRelatorioVO, vQtdeRel, valorParametro);
			if(retorno != null){
				DocumentosPacienteVO documentosRelatorioMedico = setarDadosDocumentoPaciente(retorno, mamRelatorioVO.getIndImpresso().equals("S") ? Boolean.TRUE : Boolean.FALSE);
				
				Object[] result = new Object[4];
				result = this.prescricaoMedicaFacade.buscaConsProf(null, null);
				mamRelatorioVO.setNomeMedico(result[1].toString());
				StringBuilder usuario = new StringBuilder(400);
				if (result[1] != null) {
					usuario.append(result[1]);
				}
				if (result[2] != null) {
					usuario.append(" - ")
							.append(result[2]);
				}
				if (result[1] != null) {
					usuario.append(" " + result[3]);
				}
				mamRelatorioVO.setUsuario(usuario.toString());
				documentosRelatorioMedico.setMamRelatorioVO(mamRelatorioVO);
				listaDocumentos.add(documentosRelatorioMedico);
			}
		}
	}
	
	/**
	 * ORADB: Procedure p_popula_tela_alta
	 * @throws ApplicationBusinessException 
	 *  
	*/
	public void obterListaAltaSumarios(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos) throws ApplicationBusinessException{
		String vTexto;
		Short vQtdeAls=Short.valueOf("0");
		////////////////////
		for (MamAltaSumario altaSumario : this.mamAltasSumarioDAO.verificarExisteAltaSumario(conNumero)) {
			if(altaSumario.getPendente().equals(DominioIndPendenteDiagnosticos.V)){
				if((altaSumario.getPendente().equals(DominioIndPendenteDiagnosticos.P) || verificarPerfilMarcacao()) &&
				   !this.validarProcesso(null, null, retornarCodigoProcessoAlta())  ){
					return;	
				}
			}
		
			/////////////////////
			vQtdeAls++;
			if(vQtdeAls==1){
				vTexto="Alta";
			}else{
				vTexto="Alta - "+vQtdeAls.toString();
			}
			/////////////////////						
			if (vTexto != null && !vTexto.isEmpty()) {
				DocumentosPacienteVO documentoEvolucao = setarDadosDocumentoPaciente(vTexto, altaSumario.getImpresso());
				documentoEvolucao.setAltaSumario(altaSumario);
				listaDocumentos.add(documentoEvolucao);					
			}
		}
	}
	
	/**
	 * ORADB: Procedure p_popula_tela_laudo
	 * @throws ApplicationBusinessException 
	 *  
	*/
	public void obterListaLaudoAih(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos, String vSituacaoCancelado, String vSituacaoPendente, Short paramSolicitacaoExames) throws ApplicationBusinessException{
	Integer vQtdeLai=0;
	String vTexto;
	
	List<MamLaudoAih> listaLaudoAih = mamLaudoAihDAO.verificarLaudoAihParaImpressao(conNumero);
	for (MamLaudoAih laudoAih : listaLaudoAih) {
		 if(!laudoAih.getIndPendente().equals(DominioIndPendenteLaudoAih.V)){
			 if( (laudoAih.getIndPendente().equals(DominioIndPendenteLaudoAih.P) || verificarPerfilMarcacao()) && !validarProcesso(null, null, retornarCodigoProcessoLaudo())){
				 return;
			 }
		 }
		 vQtdeLai++;
		 if(vQtdeLai == 1){
			 vTexto="Laudo AIH";
		 }else{
			 vTexto="Laudo AIH - "+vQtdeLai.toString();
		 }
		 if (vTexto != null && !vTexto.isEmpty()) {
			 DocumentosPacienteVO documentoEvolucao = setarDadosDocumentoPacienteLaudoAih(vTexto, laudoAih.getIndImpresso(), laudoAih);
			 documentoEvolucao.setLaudoAih(laudoAih);
			 listaDocumentos.add(documentoEvolucao);
		 }			 
	}
	}
	
public DocumentosPacienteVO setarDadosDocumentoPacienteLaudoAih(String descricao, Boolean imprimiu, MamLaudoAih laudoAih) throws ApplicationBusinessException {
	DocumentosPacienteVO documento = new DocumentosPacienteVO();
	documento.setDescricao(descricao);
	if(imprimiu) {
		documento.setImprimiu(Boolean.TRUE);
		documento.setSelecionado(Boolean.FALSE);
	} else {
		documento.setImprimiu(Boolean.FALSE);
		documento.setSelecionado(Boolean.TRUE);
	}
	
	return documento;
}
	
	/**
 * ORADB: Procedure p_popula_tela_laudo_solic
 * @throws ApplicationBusinessException 
 *  
*/
public void obterListaDocumentosSolicitacaoExames(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos, String vSituacaoCancelado, String vSituacaoPendente, Short paramSolicitacaoExames, MamLaudoAih laudoAih) throws ApplicationBusinessException{
	Boolean vTemExame = Boolean.FALSE;
	Boolean vTemExamePendente=Boolean.FALSE;
	String vSituacaoItem = vSituacaoCancelado;
	StringBuffer vTexto = new StringBuffer("");
	Integer vTamanhoUtilizado;
	Integer vTamanhoLivre;
	final Integer vPosFim = 55;
	List<AelExames> listaVDescricao = new ArrayList<AelExames>();
	StringBuffer vDescricaoExames= new StringBuffer("");
	
	List<AelItemSolicitacaoExames> listaExamePendente = aelItemSolicitacaoExameDAO.verificaExamePendente(conNumero, vSituacaoItem);
	
	for(AelItemSolicitacaoExames exame : listaExamePendente){
		vTemExame = Boolean.TRUE;
		if(exame.getSituacaoItemSolicitacao().getCodigo() == vSituacaoPendente){
			vTemExamePendente=Boolean.TRUE;
		}
	}
	if(vTemExame){
		if(vTemExamePendente && validarProcesso(null, null, paramSolicitacaoExames) == Boolean.FALSE){
			return;
		}
	}
	List<VerificaItemSolicitacaoExamesVO> listaSolicitacaoExame = aelItemSolicitacaoExameDAO.verificaSolicitacaoExame(conNumero, vSituacaoItem);
	for (VerificaItemSolicitacaoExamesVO aelItemSolicitacaoExames : listaSolicitacaoExame) {
		if(existeLaudoAih(aelItemSolicitacaoExames.getSoeSeq())){//Dados da F1 - _get_imp_lau_sol (r_soe.seq) = 'S'
			vTexto.append("Informações complementares - ").append(aelItemSolicitacaoExames.getSoeSeq()).append(" - ");
			vTamanhoUtilizado = vTexto.length();
			vTamanhoLivre = vPosFim - vTamanhoUtilizado - 1;
			listaVDescricao = aelExamesDAO.obterDescricaoExame(aelItemSolicitacaoExames.getSoeSeq(), vSituacaoItem);
			for (AelExames vDescricao : listaVDescricao) {
				vDescricaoExames.append(vDescricao.getDescricao());//verificar se return unique
				if(listaVDescricao!=null){
					vDescricaoExames.append(", ").append(vDescricao);
					if(listaVDescricao!=null){
						vDescricaoExames.append(", ").append(vDescricao);
					}
				}
			}				
			vTexto.append(vDescricaoExames.substring(0, vTamanhoLivre));
			if(!vTexto.equals("") ){
				DocumentosPacienteVO documentoSolicitacaoExame = setarDadosDocumentoPaciente(vTexto.toString(), aelItemSolicitacaoExames.getIndInfComplImp().equals("S"));
				AelSolicitacaoExames solicitacaoExame = aelSolicitacaoExameDAO.obterPeloId(aelItemSolicitacaoExames.getSoeSeq());
				documentoSolicitacaoExame.setLaudoAIHSolicVO(solicitacaoExame);
				documentoSolicitacaoExame.setMaterialSolicitado(laudoAih.getMaterialSolicitado());
				documentoSolicitacaoExame.setPacCodigo(laudoAih.getPacCodigo());
				listaDocumentos.add(documentoSolicitacaoExame);	
			}				
		}
	}
	
}

/**
 * ORADB: Function MAMC_GET_IMP_LAU_SOL
 * @throws ApplicationBusinessException 
 *  
*/
private Boolean existeLaudoAih(Integer soeSeq){
	Boolean vImpLaudo;
	String vSituacaoPendente ="";
	String vSituacaoCancelado="";
			
	vSituacaoPendente = getParametroFacade().obterAghParametroPorNome(AghuParametrosEnum.P_SITUACAO_PENDENTE.toString()).getVlrTexto();
	vSituacaoCancelado= getParametroFacade().obterAghParametroPorNome(AghuParametrosEnum.P_SITUACAO_CANCELADO.toString()).getVlrTexto();
	vImpLaudo=Boolean.FALSE;
	
	List<ItemSolicitacaoExameAtendimentoVO> listaIse = aelItemSolicitacaoExameDAO.obterCursorItemSolicitacaoExame(soeSeq, vSituacaoCancelado, vSituacaoPendente);
	for (ItemSolicitacaoExameAtendimentoVO item : listaIse) {
		if(aelExQuestionarioOrigensDAO.obterCursorResposta(item.getUfeEmaExaSigla(), item.getUfeEmaManSeq(), item.getCspCnvCodigo(), item.getOrigemAtendimento())){
			if(aelRespostaQuestaoDAO.obterRespostaInformada(item.getSoeSeq(),item.getSeqp())){
				vImpLaudo=Boolean.TRUE;
			}
		}
	}
	return vImpLaudo;		
}

	/**
	 * Atualiza MamSolicitacaoProcedimento indicando que foi impresso. #43087
	 * 
	 * @param seq
	 */
	public void atualizarIndImpressoSolicitacaoProcedimento(Long seq) {
		MamSolicProcedimento solicitacaoProcedimento = mamSolicProcedimentoDAO.obterPorChavePrimaria(seq);
		solicitacaoProcedimento.setIndImpresso("S");
		mamSolicProcedimentoDAO.atualizar(solicitacaoProcedimento);
	}

	/**
	 * Verifica se existe documentos de solicitação hemoterapicas para imprimir em pesquisarPacientesAgendadosController #43087
	 * 
	 * @param consulta
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarSolicHemoterapia(AacConsultas consulta,Boolean vTemp, Boolean verificarProcesso) throws ApplicationBusinessException {
		List<MamSolicProcedimento> listaSolicProcedimento = getMamSolicProcedimentoDAO().obterDocumentosSolicitacaoProcedimento(consulta.getNumero());
		for (MamSolicProcedimento solicProcedimento : listaSolicProcedimento) {
			if (solicProcedimento.getIndPendente().equals("V") || (solicProcedimento.getIndPendente().equals("P") && (!verificarProcesso || validarProcesso(null, null, this.retornarCodigoProcessoHemoterapia())))) {
				vTemp = Boolean.TRUE;
			}
		}
		return vTemp;
	}

	/**
	 * Retorna o código do processo que representa a hemoterapia #43087
	 * 
	 * @ORADB: Package MAMK_PROCESSOS.MAMC_GET_PROC_ANAM
	 * 
	 */
	private Short retornarCodigoProcessoHemoterapia() throws ApplicationBusinessException {
		Short codigoProcessoHemoterapia = 0;
		AghParametros parametroCodProcessoHemoterapia = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_SOLIC_HEMO);
		if (parametroCodProcessoHemoterapia.getVlrNumerico() != null) {
			codigoProcessoHemoterapia = parametroCodProcessoHemoterapia.getVlrNumerico().shortValue();
		}
		return codigoProcessoHemoterapia;
	}

	/**
	 * @ORADB: P_POPULA_TELA_SOLIC_HEMO Este procedimento tem o objetivo de incluir um novo registro, relacionado ao documento, na lista de documentos
	 *         a serem impresoss (Modal Impressões do ambulatorio) #43087
	 * @param conNumero
	 * @param listaDocumentos
	 * @throws ApplicationBusinessException
	 */
	public void obterListaDocumentosHemoterapia(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos) throws ApplicationBusinessException {
		Short paramProcessoHemo = retornarCodigoProcessoHemoterapia();
		DocumentosPacienteVO documentoSolicProcedimento;
		Integer qtdSolicProcedimento = 1;
		boolean imprimiu;
		List<MamSolicProcedimento> listaSolicProcedimento = getMamSolicProcedimentoDAO().obterDocumentosSolicitacaoProcedimento(conNumero);
		for (MamSolicProcedimento solicProcedimento : listaSolicProcedimento) {
			imprimiu = false;
			if (!solicProcedimento.getIndPendente().equals("V")) {
				if ((solicProcedimento.getIndPendente().equals("P") || verificarPerfilMarcacao()) && !validarProcesso(null, null, paramProcessoHemo)) {
					break; // Tem um return na procedure original
				}
			}
			if (solicProcedimento.getIndImpresso().equals("S")) {
				imprimiu = true;
			}
			if (listaSolicProcedimento.size() == 1) {
				documentoSolicProcedimento = setarDadosDocumentoPaciente("Solicitação procedimentos ", imprimiu);
			} else {
				documentoSolicProcedimento = setarDadosDocumentoPaciente("Solicitação procedimentos - " + qtdSolicProcedimento, imprimiu);
			}
			documentoSolicProcedimento.setMamSolicProcedimento(solicProcedimento);
			// VO Relatorio MamSolicProcedimensolicProcedimento.getSeq();
			listaDocumentos.add(documentoSolicProcedimento);
			qtdSolicProcedimento++;
		}
	}
	
	/**
	 * #43029 - P1
	 * @ORADB: p_popula_tela_relatorio
	 * @param mamRelatorioVO
	 * @param vQtdeRel
	 * @param valorParametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String pPopulaTelaRelatorio(MamRelatorioVO mamRelatorioVO, Integer vQtdeRel, BigDecimal valorParametro) throws ApplicationBusinessException{
		
		String vTexto; 
		
		if(!mamRelatorioVO.getIndPendente().equals("V")){
			if((mamRelatorioVO.getIndPendente().equals("P") || verificarPerfilMarcacao()) && 
					!validarProcesso(null, null, valorParametro.shortValue())){
				return null;
			}
		}
		
		if(vQtdeRel == 1){
			vTexto = "Relatório";
		}else{
			vTexto = "Relatório - ".concat(vQtdeRel.toString());
		}
		return vTexto;
	}

	public DocumentosPacienteVO setarDadosDocumentoPaciente(String descricao, Boolean imprimiu) {
		DocumentosPacienteVO documento = new DocumentosPacienteVO();

		documento.setDescricao(descricao);
		if (imprimiu) {
			documento.setImprimiu(Boolean.TRUE);
			documento.setSelecionado(Boolean.FALSE);
		} else {
			documento.setImprimiu(Boolean.FALSE);
			documento.setSelecionado(Boolean.TRUE);
		}

		return documento;
	}

	/**
	 * Verifica se o usuário pode ou não validar um determinado processo
	 * 
	 * ORADB: Package MAMK_PERFIL.MAMC_VALIDA_PROC_E
	 * @param pSerVinCodigo, pSerMatricula, pRocSeq, pEspSeq
	 * 
	 */
	public Boolean validarProcessoE(Short pSerVinCodigo, Integer pSerMatricula, Short pRocSeq, Short pEspSeq) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		String login;
		Boolean vAchouNovo;
		Boolean vValida = true;

		if (pSerVinCodigo != null && pSerMatricula != null) {
			RapServidores servidor = getRegistroColaboradorFacade().obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(pSerMatricula, pSerVinCodigo);
			login = servidor.getUsuario();
		} else {
			login = servidorLogado.getUsuario();
		}

		vValida = getCascaFacade().validarPermissaoPorServidorEProcessos(login, pRocSeq, pEspSeq);
		if (!vValida) {
			vAchouNovo = false;
		} else {
			vAchouNovo = true;
		}

		if (!vAchouNovo) {
			vValida = getCascaFacade().validarPermissaoPorServidorERocSeq(login, pRocSeq);
		}

		return vValida;
	}

	/**
	 * Verifica se o usuário logado tem perfil de marcação
	 * 
	 * ORADB: C_TEM_PERFIL_MARCACAO
	 * 
	 */
	public Boolean verificarPerfilMarcacao() throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		// Busca parâmetro que indica o perfil de marcação
		String vPerfilMarcacao = null;
		AghParametros parametroSituacaoMarcacao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PERFIL_MARCACAO);
		if (parametroSituacaoMarcacao.getVlrTexto() != null) {
			vPerfilMarcacao = parametroSituacaoMarcacao.getVlrTexto();
		}

		return getCascaFacade().usuarioTemPerfil(servidorLogado.getUsuario(), vPerfilMarcacao);
	}

	/**
	 * Retorno o código do processo que representa o relatório
	 * 
	 * ORADB: Package MAMK_PROCESSOS.MAMC_GET_PROC_ALTA
	 *   
	 */
	public Short retornarCodigoProcessoAlta() throws ApplicationBusinessException {
		Short codigoProcessoAlta = 0;
		AghParametros parametroCodProcessoAlta = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_ALTA);
		if(parametroCodProcessoAlta.getVlrNumerico()!=null){
			codigoProcessoAlta = parametroCodProcessoAlta.getVlrNumerico().shortValue();	
		}
		return codigoProcessoAlta;
	}
	
	/**
	 * Retorno o código do processo que representa o relatório
	 * 
	 * ORADB: Package MAMK_PROCESSOS.MAMC_GET_PROC_LAUDO
	 *   
	*/
	public Short retornarCodigoProcessoLaudo() throws ApplicationBusinessException {
		Short codigoProcessoLaudo = 0;
		AghParametros parametroCodProcessoLaudo = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_LAUDO_AIH);
		if(parametroCodProcessoLaudo.getVlrNumerico()!=null){
			codigoProcessoLaudo = parametroCodProcessoLaudo.getVlrNumerico().shortValue();	
		}
		return codigoProcessoLaudo;
	}
	
	//Tratamento Fisiátrico
	public Boolean verificarImpressaoTratamentoFisiatrico(AacConsultas consulta, Boolean vTemImp) throws ApplicationBusinessException {		
		
		Integer tptSeqFis = null;
		AghParametros parametro = new AghParametros();
		parametro.setNome(AghuParametrosEnum.P_TIPO_TRAT_FISIOTERAPIA.toString());
		List<AghParametroVO> listaParametros = parametroSistemaFacade.pesquisaParametroList(0, 1, null, false, parametro);
		if (listaParametros != null && !listaParametros.isEmpty()) {
			if (listaParametros.get(0).getVlrNumerico() != null) {
				tptSeqFis = listaParametros.get(0).getVlrNumerico().intValue();
			}
		}
		
		AtendimentoPrescricaoPacienteVO cFis = null;

		cFis = aghAtendimentoDAO.obterSequenciaisAtendimentoPrescricaoPaciente(consulta.getNumero(), tptSeqFis);	
		
		if (cFis != null) {
			vTemImp = true;
			return vTemImp;
		}
		
			return vTemImp;
		}
				
	/**
	 * @ORADB p_popula_tela_fisiatria
	 * @param conNumero
	 * @param listaDocumentos
	 * @throws ApplicationBusinessException
	 */
	public void obterListaDocumentosPacienteTratamentoFisiatrico(Integer conNumero, List<DocumentosPacienteVO> listaDocumentos) throws ApplicationBusinessException {
		Integer tptSeqFis = null;
		AghParametros parametro = new AghParametros();
		parametro.setNome(AghuParametrosEnum.P_TIPO_TRAT_FISIOTERAPIA.toString());
		List<AghParametroVO> listaParametros = parametroSistemaFacade.pesquisaParametroList(0, 1, null, false, parametro);
		if (listaParametros != null && !listaParametros.isEmpty()) {
			if (listaParametros.get(0).getVlrNumerico() != null) {
				tptSeqFis = listaParametros.get(0).getVlrNumerico().intValue();
			}
		}
		
		AtendimentoPrescricaoPacienteVO cFis = null;

		cFis = aghAtendimentoDAO.obterSequenciaisAtendimentoPrescricaoPaciente(conNumero, tptSeqFis);
		
		String texto = "Tratamento Fisiátrico";
		
		Boolean indImpresso = null;		
		if (cFis == null || cFis.getIndPrcrImpressao() == null) {
			indImpresso = Boolean.FALSE;
		} else {
			indImpresso = cFis.getIndPrcrImpressao();
		}
		
		List<DescricaoCodigoComplementoCidVO> listaDescricaoCodigoComplementoCidVO = new ArrayList<DescricaoCodigoComplementoCidVO>();
		MptPrescricaoPaciente dataPrevExecucaoTratamentoObservacao = new MptPrescricaoPaciente();
		List<ImagemModalidadeOrientacaoVO> listaImagemModalidadeOrientacaoVO = new ArrayList<ImagemModalidadeOrientacaoVO>();
		QtdSessoesTratamentoVO qtdSessoesTratamentoVO = new QtdSessoesTratamentoVO();
		MptPrescricaoPaciente prescricaoPaciente = new MptPrescricaoPaciente();
		AipPacientes aipPacientes = new AipPacientes();
		
		if (cFis != null) {
			listaDescricaoCodigoComplementoCidVO = aghCidDAO.obterDescricaoCidPorAtendimentoPrescricaoPaciente(cFis.getSeqAtd(), cFis.getSeqPte());
			dataPrevExecucaoTratamentoObservacao = mptPrescricaoPacienteDAO.obterDataPrevisaoExecucaoPorNumeroAtendimento(cFis.getSeqAtd(), cFis.getSeqPte());
			listaImagemModalidadeOrientacaoVO = mptItemPrcrModalidadeDAO.obterImagemModalidadeOrientacao(cFis.getSeqAtd(), cFis.getSeqPte());
			qtdSessoesTratamentoVO = mptPrescricaoPacienteDAO.obterQuantidadeSessoesTratamento(cFis.getSeqAtd(), cFis.getSeqPte());
			aipPacientes = aipPacientesDAO.obterPacientePorAtendimento(cFis.getSeqAtd());			
			prescricaoPaciente = mptPrescricaoPacienteDAO.obterPrescricaoPorNumeroConsulta(conNumero);
		
			RelatorioTratamentoFisiatricoVO relatorioTratamentoFisiatricoVO = new RelatorioTratamentoFisiatricoVO();
			relatorioTratamentoFisiatricoVO.setListaDescricaoCodigoComplementoCidVO(listaDescricaoCodigoComplementoCidVO);
			relatorioTratamentoFisiatricoVO.setDataPrevExecucaoTratamentoObservacao(dataPrevExecucaoTratamentoObservacao);
			relatorioTratamentoFisiatricoVO.setListaImagemModalidadeOrientacaoVO(listaImagemModalidadeOrientacaoVO);
			relatorioTratamentoFisiatricoVO.setQtdSessoesTratamentoVO(qtdSessoesTratamentoVO);
			relatorioTratamentoFisiatricoVO.setPrescricaoPaciente(prescricaoPaciente);
			relatorioTratamentoFisiatricoVO.setAipPacientes(aipPacientes);
					
			DocumentosPacienteVO documentoTratamentoFisiatrico = setarDadosDocumentoPaciente(texto, indImpresso);
			documentoTratamentoFisiatrico.setRelatorioTratamentoFisiatricoVO(relatorioTratamentoFisiatricoVO);
			listaDocumentos.add(documentoTratamentoFisiatrico);
		}
	}
	
	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public ICascaFacade getCascaFacade() {
		return cascaFacade;
	}

	public void setCascaFacade(ICascaFacade cascaFacade) {
		this.cascaFacade = cascaFacade;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}
	protected MamSolicProcedimentoDAO getMamSolicProcedimentoDAO() {
		return mamSolicProcedimentoDAO;
	}
}