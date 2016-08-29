package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoEtiquetas;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.exames.business.AelAmostraItemExamesRN;
import br.gov.mec.aghu.exames.business.AelAmostrasRN;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisaXExameDAO;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisasDAO;
import br.gov.mec.aghu.exames.dao.AelAmostraCtrlEtiquetasDAO;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelAmostrasDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemHorarioAgendadoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.ConfirmacaoImpressaoEtiquetaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.UnfExecutaSinonimoExameVO;
import br.gov.mec.aghu.exames.vo.ImprimeEtiquetaVO;
import br.gov.mec.aghu.impressao.ISistemaImpressaoCUPS;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelAgrpPesquisaXExame;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelAmostraCtrlEtiquetas;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelTipoAmostraExameId;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghDocumentoContingencia;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
@SuppressWarnings("PMD.ExcessiveClassLength")
public class EtiquetasON extends BaseBusiness {

	private static final String UP_XZ = "^XZ";

	private static final String UP_XA = "^XA";

	private static final String UP_FS = "^FS";

	private static final String UP_PRA = "^PRA";

	private static final String UP_XA_IDR_TXT_XZ = "^XA^IDR:*.TXT^XZ";
	
	private static final String SITUACAO_EM_COLETA = "EC";
	
	private static final String PERMISSAO_COLETA_AMOSTRA = "coletarAmostraII";

	@Inject
	private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;
	
	@EJB
	private ItemSolicitacaoExameRN itemSolicitacaoExameRN;
	
	@EJB
	private AelAmostraItemExamesRN aelAmostraItemExamesRN;
	
	private static final Log LOG = LogFactory.getLog(EtiquetasON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;
	
	@Inject
	private AelAmostraCtrlEtiquetasDAO aelAmostraCtrlEtiquetasDAO;
	
	@Inject
	private AelItemHorarioAgendadoDAO aelItemHorarioAgendadoDAO;
	
	@Inject
	private AelAmostrasDAO aelAmostrasDAO;
	
	@Inject
	private AelAmostrasRN aelAmostrasRN;
	
	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;
	
	@Inject
	private ICascaFacade cascaFacade;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelAgrpPesquisasDAO aelAgrpPesquisasDAO;
	
	@Inject
	private AelAgrpPesquisaXExameDAO aelAgrpPesquisaXExameDAO;
	
	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;
	
 	@EJB
	private ISistemaImpressaoCUPS sistemaImpressaoCups;	
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private ItemSolicitacaoExameON itemSolicitacaoExameON;	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	@EJB
	private EtiquetasRedomeON etiquetasRedomeON;

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9071814330259762784L;
	private static final String[] NUMEROS_ROMANOS_ACEITOS = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
	private static final char NEW_LINE = 10;
	
	public enum EtiquetasONExceptionCode implements BusinessExceptionCode {
		AEL_00895,
		MPM_01237,
		ERRO_UNIDADE_SEM_CARACT_IMPRIMIR_AMOSTRA,
		ERRO_IMPRESSORA_ETIQUETA_NAO_ENCONTRADA,
		ERRO_IMPRIMIR_ETIQUETA_NUM_AMOSTRA;
	}
	
	public enum Origens implements BusinessExceptionCode{
		A, X, C; 
	}
	
	/**
	 * ORADB AELP_GERA_ETIQUETAS (RN1 da estória #5422)
	 * @param isSolicitacaoNova 
	 * 
	 */
	public Integer gerarEtiquetas(final AelSolicitacaoExames solicitacaoExame, final AghUnidadesFuncionais unidadeTrabalho, final String nomeImpressora, String situacaoItemExame, boolean isSolicitacaoNova) throws BaseException {
		return this.gerarEtiquetas(solicitacaoExame, null, unidadeTrabalho, nomeImpressora, situacaoItemExame, isSolicitacaoNova); 
	}
	
	/**
	 * ORADB AELP_GERA_ETIQUETAS (RN1 da estória #5422)
	 * @param isSolicitacaoNova 
	 * 
	 */
	public Integer gerarEtiquetas(final AelSolicitacaoExames solicitacaoExame, final Short amoSeqp, final AghUnidadesFuncionais unidadeTrabalho, final String nomeImpressora, String situacaoItemExame, boolean isSolicitacaoNova) throws BaseException {
		return this.gerarEtiquetas(false, solicitacaoExame, amoSeqp, unidadeTrabalho, nomeImpressora, situacaoItemExame, isSolicitacaoNova);
	}
	
	/**
	 * ORADB AELP_GERA_ETIQUETAS (RN1 da estória #5422)
	 * 
	 */
	public Integer gerarEtiquetas(final boolean impressaoInternacao, final AelSolicitacaoExames solicitacaoExame, final AghUnidadesFuncionais unidadeTrabalho, final String impressoraCups, String situacaoItemExame) throws BaseException {
		return this.gerarEtiquetas(impressaoInternacao, solicitacaoExame, null, unidadeTrabalho, impressoraCups, situacaoItemExame, false); 
	}
	
	/**
	 * ORADB AELP_GERA_ETIQUETAS (RN1 da estória #5422)
	 * @param impressaoInternacao quando a impressão considera a internação. Vide: Relatório de Materiais a coletar da internação
	 * @param solicitacaoExame
	 * @param amoSeqp
	 * @param unidadeTrabalho
	 * @param isSolicitacaoNova 
	 * @param impressoraCups
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.NPathComplexity","PMD.UseStringBufferForStringAppends"})
	public Integer gerarEtiquetas(final boolean impressaoInternacao, final AelSolicitacaoExames solicitacaoExame, final Short amoSeqp,
			final AghUnidadesFuncionais unidadeTrabalho, String nomeImpressora, String situacaoItemExame, boolean isSolicitacaoNova) throws BaseException {
		
		Integer qtdAmostras = 0;
		
		//RN1.1
		//Verifica localização do paciente
		final String local = localizarPaciente(solicitacaoExame.getAtendimento());
		
		//RN1.2
		//Busca prontuário do paciente
		String pacProntuario = getExamesFacade().buscarLaudoProntuarioPaciente(solicitacaoExame);
		if(pacProntuario.equals("0")){
			pacProntuario = "s/pront";
		}
		
		//RN1.3
		//Busca nome do paciente
		final String pacNome = getExamesFacade().buscarLaudoNomePaciente(solicitacaoExame);
		
		//RN1.4
		//Mascara prontuário do paciente - Desnecessário pois método buscarLaudoProntuarioPaciente já retorna formatado
		//String prontMasc = pacProntuario;
		
		//RN1.5
		final boolean coleta = unidadeTrabalho == null ? false : this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeTrabalho.getSeq(), ConstanteAghCaractUnidFuncionais.UNID_COLETA);
		
		//RN1.6
		//Busca amostras
		final List<AelAmostras> amostras = getAelAmostrasDAO().buscarAmostrasPorSolicitacaoColetaSituacao(solicitacaoExame.getSeq(), coleta, amoSeqp, situacaoItemExame);
		
		if (amostras == null || amostras.isEmpty()) {
			throw new ApplicationBusinessException(EtiquetasONExceptionCode.AEL_00895, Severity.INFO);
		}
		
		//Busca convênio
		final FatConvenioSaudePlano convenioSaudePlano = getItemSolicitacaoExameRN().obterFatConvenioSaudePlano(solicitacaoExame);
		FatConvenioSaude convenioSaude = null;
		if (convenioSaudePlano != null) {
			convenioSaude = convenioSaudePlano.getConvenioSaude();
		}
		
		final StringBuffer acumulaZplPorAmostra = new StringBuffer();
		
		for (final AelAmostras amostra : amostras) {

				//RN1.8
				final List<AelAmostraItemExames> amostraItensExames = getAelAmostraItemExamesDAO().buscarAelAmostraItemExamesPorAmostra(solicitacaoExame.getSeq(), amostra.getId().getSeqp().intValue());

				//RN1.7
				//Busca descrição do anticoagulante ou material de análise
				String materialDesc = "";
				Integer codMateriaisDiversos = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_COD_MATERIAIS_DIVERSOS);
				Short unfSeqPato = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_UNID_PATOL_CIRG);
				
				if ( (codMateriaisDiversos!= null && codMateriaisDiversos == amostra.getMateriaisAnalises().getSeq()) 
						&& (amostraItensExames != null && !amostraItensExames.isEmpty() 
								&& amostraItensExames.get(0).getAelItemSolicitacaoExames() != null
								&& amostraItensExames.get(0).getAelItemSolicitacaoExames().getRegiaoAnatomica() != null
								&& StringUtils.isNotBlank(amostraItensExames.get(0).getAelItemSolicitacaoExames().getRegiaoAnatomica().getDescricao()))
						){
					materialDesc = amostraItensExames.get(0).getAelItemSolicitacaoExames().getRegiaoAnatomica().getDescricao();
				} else if (amostra.getAnticoagulante() != null){
					materialDesc = amostra.getAnticoagulante().getDescricao();
				}
				boolean isPatologia = false;
				if ((unfSeqPato != null && amostra.getUnidadesFuncionais() != null 
						&& unfSeqPato == amostra.getUnidadesFuncionais().getSeq()) 
						&& (amostraItensExames != null && !amostraItensExames.isEmpty() 
							&& amostraItensExames.get(0).getAelItemSolicitacaoExames() != null
							&& StringUtils.isNotBlank(amostraItensExames.get(0).getAelItemSolicitacaoExames().getDescMaterialAnalise()))){
					// Se descrição informada na solicicitação utiliza a informada pelo usuário.
					materialDesc = amostraItensExames.get(0).getAelItemSolicitacaoExames().getDescMaterialAnalise() + '/' + materialDesc;
					if (materialDesc.length() > 56){
						materialDesc = materialDesc.substring(0, 56);
					}
					isPatologia = true;
				} 
				
				
				final boolean existeTipoAmostraInternacaoTodasOrigens = this.verificarTipoAmostraInternacao(amostraItensExames, amostra);
				
				qtdAmostras += this.appendZpl(impressaoInternacao, existeTipoAmostraInternacaoTodasOrigens, amostraItensExames, acumulaZplPorAmostra, pacProntuario, convenioSaude, pacNome, materialDesc, solicitacaoExame, amostra, local, isSolicitacaoNova,isPatologia);
			
		}
		
		if (nomeImpressora != null) {
			// Envia ZPL para impressora Zebra instalada no CUPS.
			sistemaImpressaoCups.imprimir(acumulaZplPorAmostra.toString(), nomeImpressora);
		} else {
			// A impressora não valida se existe uma impressora de etiqueta na unidade, se não existir ela não imprimi a etiqueta.
			boolean gerarEtiqueta = false;
			// Envia ZPL para impressora Zebra da unidade para a impressão (para o caso em que não foi cadastrada a impressora para o microcomputador)
			sistemaImpressaoCups.imprimir(acumulaZplPorAmostra.toString(), unidadeTrabalho, TipoDocumentoImpressao.ETIQUETAS_BARRAS, gerarEtiqueta);
		}
			
		// Gera cópia de segurança das etiquetas
		this.gerarCopiaSegurancaEtiquetas(acumulaZplPorAmostra, DominioNomeRelatorio.TICKET_EXAMES);
		
		return qtdAmostras;
		
	}
	
	
	/**
	 * Acrescenta código ZPL no acumulador de ZPL por amostra
	 * @param impressaoInternacao
	 * @param existeTipoAmostraInternacaoTodasOrigens
	 * @param amostraItensExames
	 * @param acumulaZplPorAmostra ACUMULADOR DE ZPL POR AMOSTRA
	 * @param pacProntuario
	 * @param convenioSaude
	 * @param pacNome
	 * @param atcDesc
	 * @param solicitacaoExame
	 * @param amostra
	 * @param local
	 * @param isSolicitacaoNova 
	 * @return
	 * @throws BaseException
	 */
	private int appendZpl(final boolean impressaoInternacao, final boolean existeTipoAmostraInternacaoTodasOrigens, final List<AelAmostraItemExames> amostraItensExames, final StringBuffer acumulaZplPorAmostra,
			String pacProntuario, FatConvenioSaude convenioSaude, final String pacNome, String atcDesc, final AelSolicitacaoExames solicitacaoExame,
			final AelAmostras amostra, final String local, boolean isSolicitacaoNova, boolean isPatologia) throws BaseException{

		if(impressaoInternacao && !existeTipoAmostraInternacaoTodasOrigens){
			/* Nada será acrescentado ao ZPL geral 
			 * quando a impressão é da internação e o tipo de amostra for incompatível
			 */
			return 0;
		}
		
		final String siglasExames = gerarListaExames(amostraItensExames);
		final String numSala = obterNumeroSala(amostraItensExames);
		
		//Gera o ZPL
		acumulaZplPorAmostra.append(gerarZpl(numSala, new Date(), pacProntuario, convenioSaude, pacNome, atcDesc, siglasExames, solicitacaoExame, amostra, local, isSolicitacaoNova, isPatologia));
		return 1;
	}
	
	
	
	/**
	 * Se impressa etiqueta de amostra, altera a situação do item de 'a coletar' para 'em coleta' 
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @param nomeMicrocomputador
	 * @param dataFimVinculoServidor
	 * @throws BaseException 
	 * 
	 */
	public void atualizarItemSolicitacaEmColeta(Integer soeSeq, Short seqp, String nomeMicrocomputador, Date dataFimVinculoServidor) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		AelItemSolicitacaoExames itemSolicitacaoExame = getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorId(soeSeq, seqp);
		AelSitItemSolicitacoes sitItemSolicitacao = aelSitItemSolicitacoesDAO.obterPeloId(SITUACAO_EM_COLETA);
		
		itemSolicitacaoExame.setSituacaoItemSolicitacao(sitItemSolicitacao);
		getItemSolicitacaoExameRN().atualizar(itemSolicitacaoExame, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);		
	}
	
	/**
	 * Se impressa etiqueta de amostra, altera a situação da amostra de 'a coletar' para 'em coleta' 
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param amoSoeSeq
	 * @param amoSeqp
	 * @param nomeMicrocomputador
	 * @throws BaseException 
	 * 
	 */
	public void atualizarAmostraSolicitacaEmColeta(Integer iseSoeSeq, Short iseSeqp,Integer amoSoeSeq, Integer amoSeqp, String nomeMicrocomputador) throws BaseException{
		AelAmostraItemExames amostraItem = getAelAmostraItemExamesDAO().obterPorChavePrimaria(iseSoeSeq, iseSeqp, amoSoeSeq, amoSeqp);
		getAelAmostraItemExamesRN().atualizarAelAmostraItemExames(amostraItem, DominioSituacaoAmostra.M, true, true, nomeMicrocomputador);
		AelAmostras amostra = getAelAmostrasDAO().obterAelAmostras(amoSoeSeq, amoSeqp);
		if (DominioSituacaoAmostra.M.equals(amostra.getSituacao())){
			amostra.setSituacao(DominioSituacaoAmostra.M);
			getAelAmostrasRN().atualizarAelAmostra(amostra, false);
		}
	}
	
	/**
	 * Obter descrição usual do exame
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @throws BaseException 
	 * 
	 */
	public String obterDescricaoUsualExame(Integer soeSeq, Short seqp){
		String retorno = "";
		
		AelItemSolicitacaoExames itemSolicitacaoExame = getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorId(soeSeq, seqp);
		if (itemSolicitacaoExame!=null && itemSolicitacaoExame.getExame()!=null && itemSolicitacaoExame.getExame().getDescricao()!=null){
			retorno = itemSolicitacaoExame.getExame().getDescricao();
		}
		return retorno;
	}
	
	/**
	 * Verifica se usuario logado tem permissao para coletar amostra de exames
	 * @param login
	 * 
	 */
	public Boolean verificarUsuarioLogadoColetador(String login){
		return cascaFacade.usuarioTemPermissao(login, PERMISSAO_COLETA_AMOSTRA);
	}
	
	
	/**
	 * Verifica se usuario solicitador tem permissao para coletar amostra de exames
	 * @param solicitacao
	 * @param item
	 * 
	 */
	public Boolean verificarUsuarioSolicitanteColetador(Integer soeSeq, Short seqp){
		
		Boolean retorno = Boolean.FALSE; 
		AelExtratoItemSolicitacao extrato = aelExtratoItemSolicitacaoDAO.obterUltimoItemSolicitacaoSitCodigo(soeSeq, seqp, SITUACAO_EM_COLETA);
		
		if (extrato!=null){ //Retorna false caso nunca tenha entrado na situação "EM COLETA"
			retorno = cascaFacade.usuarioTemPermissao(extrato.getServidor().getUsuario(), PERMISSAO_COLETA_AMOSTRA);
		}		
		
		return retorno;		
	}
	
	/**
	 * 
	 * @param amostraItensExames
	 * @param amostra
	 * @return
	 */
	private boolean verificarTipoAmostraInternacao(final List<AelAmostraItemExames> amostraItensExames, final AelAmostras amostra) {

		AelTipoAmostraExame tipoAmostraExame = null;

		for (AelAmostraItemExames amostraItemExames : amostraItensExames) {

			AelTipoAmostraExameId id = new AelTipoAmostraExameId();
			
			if(amostraItemExames.getAelItemSolicitacaoExames().getAelExameMaterialAnalise() == null){
				Object[] exame  = this.getAelAmostraItemExamesDAO().buscaIdMateriasAnalizeParaGerarRelatorio(amostraItemExames.getAelItemSolicitacaoExames());
				id.setEmaExaSigla((String) exame[0]);
				id.setEmaManSeq((Integer) exame[1]);
			}else {
				id.setEmaExaSigla(amostraItemExames.getAelItemSolicitacaoExames().getAelExameMaterialAnalise().getId().getExaSigla());
				id.setEmaManSeq(amostraItemExames.getAelItemSolicitacaoExames().getAelExameMaterialAnalise().getId().getManSeq());
			}
			id.setManSeq(amostra.getMateriaisAnalises().getSeq());
			id.setOrigemAtendimento(DominioOrigemAtendimento.T);

			tipoAmostraExame = this.getAelTipoAmostraExameDAO().obterPorChavePrimaria(id);

			if (tipoAmostraExame != null) {
				break;
			}
		}

		return tipoAmostraExame != null && DominioResponsavelColetaExames.C.equals(tipoAmostraExame.getResponsavelColeta());
	}
	
	/**
	 * Gera cópia de de segurança das etiquetas
	 * @param zpl
	 * @param nomeArquivo
	 * @throws BaseException
	 */
	public void gerarCopiaSegurancaEtiquetas(final StringBuffer zpl, final DominioNomeRelatorio nomeArquivo) throws BaseException {

		if (zpl != null && StringUtils.isNotEmpty(zpl.toString())) {

			AghDocumentoContingencia documentoContingencia = new AghDocumentoContingencia();

			byte[] arquivo = zpl.toString().getBytes(); // Obtém o array de bytes da String ZPL
			documentoContingencia.setArquivo(arquivo); // Seta o array de bytes da String ZPL
			documentoContingencia.setFormato(DominioMimeType.TXT); // Seta formato como TXT
			documentoContingencia.setNome(nomeArquivo.getDescricao()); // Seta o nome do arquivo

			this.getAghuFacade().persistirDocumentoContigencia(documentoContingencia);
			this.flush();

		}

	}
		
	protected String gerarListaExames(final List<AelAmostraItemExames> amostraItensExames) {
		String siglasExames = "";
		for(final AelAmostraItemExames amostraItemExame : amostraItensExames) {
			//RN1.10
			String siglaExamesEdit;
			if(!siglasExames.equals("")) {
				siglaExamesEdit = siglasExames + "," + amostraItemExame.getAelItemSolicitacaoExames().getExame().getSigla();
			} else {
				siglaExamesEdit = amostraItemExame.getAelItemSolicitacaoExames().getExame().getSigla();
			}
			
			if(siglaExamesEdit.length() <= 100) {
				siglasExames = siglaExamesEdit; 
			} else {
				break;
			}
		}
		
		return siglasExames;
	}
	
	protected String obterNumeroSala(final List<AelAmostraItemExames> amostraItensExames) {
		String numSala = "";
		if(!amostraItensExames.isEmpty()) {
			//RN1.9
			//Busca número da sala executora do exame (busca do primeiro, dado que na lógica original era sempre sobrescrito ao longo de uma iteração)
			final List<AelItemHorarioAgendado> itensHorarioAgendado = getAelItemHorarioAgendadoDAO().buscarPorItemSolicitacaoExame(amostraItensExames.get(0).getAelItemSolicitacaoExames());
			if(itensHorarioAgendado == null || itensHorarioAgendado.isEmpty()) {
				numSala = "";
			} else {
				numSala = itensHorarioAgendado.get(0).getHorarioExameDisp().getGradeAgendaExame().getSalaExecutoraExames().getNumero(); //Deve haver apenas 1
			}
		}
		
		return numSala;
	}
	
	/**
	 * ORADB FUNCTION AELC_LOCALIZA_PAC
	 * 
	 * @throws ApplicationBusinessException 
	 */
	public String localizarPaciente(final AghAtendimentos atendimento) throws ApplicationBusinessException {
		if(atendimento != null) {
			if(atendimento.getOrigem() == DominioOrigemAtendimento.A) {
				if(this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)) {
					return "U:EMG";
				} else {
					if(atendimento.getUnidadeFuncional() != null) {
						return atendimento.getUnidadeFuncional().getSigla();
					} else { 
						return "U:AMB";// nunca vai acontecer pois a unidade funcional não pode ser nula
					}
				}
			} else if(atendimento.getOrigem() == DominioOrigemAtendimento.X) {
				return "U:EXT";
			} else if(atendimento.getOrigem() == DominioOrigemAtendimento.D) {
				return "U:DOA";
			} else if(atendimento.getOrigem() == DominioOrigemAtendimento.C) {
				return "U:CIR";
			} else if(atendimento.getOrigem() == DominioOrigemAtendimento.U) {
				if(atendimento.getUnidadeFuncional() != null) {
					return atendimento.getUnidadeFuncional().getSigla();
				} else {
					return "   ";// nunca vai acontecer pois a unidade funcional não pode ser nula
				}
			} else if(atendimento.getLeito() != null) {
				return atendimento.getLeito().getLeitoID();
			} else if(atendimento.getQuarto() != null) {
				return atendimento.getQuarto().getDescricao();
			} else if(atendimento.getUnidadeFuncional() != null) {
				return "U:" + atendimento.getUnidadeFuncional().getAndar() + (atendimento.getUnidadeFuncional().getIndAla() != null ? " " + atendimento.getUnidadeFuncional().getIndAla().toString() : "");
			} else {
				throw new ApplicationBusinessException(EtiquetasONExceptionCode.MPM_01237);
			}
		} else {
			return "   ";
		}
	}
	
	/**
	 * Método para geração do código ZPL para impressão de códigos de barras.
	 * 
	 * ORADB FUNCTION AELC_GERA_ZPL
	 * 
	 * @param local
	 * @param amostra  
	 * @param solicitacaoExame 
	 * @param exames 
	 * @param anticoagulante 
	 * @param nome 
	 * @param convenioSaude 
	 * @param prontuario 
	 * @param data 
	 * @param sala
	 * @param isSolicitacaoNova 
	 * @throws BaseException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity","PMD.AvoidArrayLoops"})
	public String gerarZpl(final String sala, final Date data, final String prontuario, final FatConvenioSaude convenioSaude, final String nome, String descMaterial, final String exames, final AelSolicitacaoExames solicitacaoExame, final AelAmostras amostra, final String local, boolean isSolicitacaoNova, boolean isPatologia) throws BaseException {
		
		final String soeSeqFinal = StringUtils.leftPad(solicitacaoExame.getSeq().toString(), 7, '0'); //Formata número da solicitação com 8 casas
		final String amoSeqFinal = StringUtils.leftPad(amostra.getId().getSeqp().toString(), 3, '0'); //Formata número da amostra com 3 casas
		String nomeFinal = nome.length() > 24 ? nome.substring(0, 24) : nome; //Limita nome para até 24 caracteres
		

		String salaFinal = "";
		//String dataFinal = "";
		String prontuarioFinal = "";
		Date dataNumeroUnico = amostra.getDtNumeroUnico();
		
		if (!isSolicitacaoNova && possuiCaracteristica(amostra.getUnidadesFuncionais(), ConstanteAghCaractUnidFuncionais.GERA_NRO_UNICO)) {
		//if(amostra.getUnidadesFuncionais().possuiCaracteristica(ConstanteAghCaractUnidFuncionais.GERA_NRO_UNICO)) {
			if(amostra.getNroUnico() == null) {
				if(!DominioSituacaoAmostra.G.equals(amostra.getSituacao()) && !DominioSituacaoAmostra.C.equals(amostra.getSituacao())) {
					throw new ApplicationBusinessException(EtiquetasONExceptionCode.ERRO_IMPRIMIR_ETIQUETA_NUM_AMOSTRA);
				}
			}
			
			//nomeFinal = formatarNome(nome);
			nomeFinal = nome.length() > 30 ? nome.substring(0, 30) : nome; //Limita nome para até 30 caracteres
			//dataFinal = DateUtil.dataToString(data, "dd/MM/yyyy");
			if(prontuario != null) {
				if(prontuario.contains("pront")){ //Verificação para que se houver a palavra 'pront' não imprimir o 'P'
					prontuarioFinal = prontuario;
				}else{
					prontuarioFinal = "P: " + prontuario;	
				}
			}
		} else {
			if (possuiCaracteristica(amostra.getUnidadesFuncionais(), ConstanteAghCaractUnidFuncionais.UNID_BANCO_SANGUE)) {
			//if(amostra.getUnidadesFuncionais().possuiCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_BANCO_SANGUE)) {
				nomeFinal = nome.length() > 30 ? nome.substring(0, 30) : nome; //Limita nome para até 30 caracteres

				
				if(solicitacaoExame.getSeq() == 0) { //quando do banco de sangue, não gera solicitação (é simbolicamente atribuído 0)
					//dataFinal = prontuario;
					dataNumeroUnico = data;
				} else {
					//dataFinal = DateUtil.dataToString(data, "dd/MM/yyyy");
					if(prontuario != null) {
						if(prontuario.contains("pront")){ //Verificação para que se houver a palavra 'pront' não imprimir o 'P'
							prontuarioFinal = prontuario;
						}else{
							prontuarioFinal = "P: " + prontuario;	
						}
					}
				}
			} else {
				dataNumeroUnico = data;
				if(prontuario != null) {
					if(prontuario.contains("pront")){ //Verificação para que se houver a palavra 'pront' não imprimir o 'P'
						prontuarioFinal = prontuario;
					}else{
						prontuarioFinal = "P: " + prontuario;	
					}
				}
			}
		}
		
		if(sala != null && !sala.equals("")) {
			salaFinal = "S:" + sala;
		}
		descMaterial = StringUtil.subtituiAcentos(descMaterial);
		String tempoColeta = "";
		String descMaterialPts[] = {"","","",""};
	
		if(amostra.getTempoIntervaloColeta().intValue() == 0 && amostra.getUnidTempoIntervaloColeta() == null) {
			tempoColeta = " ";
		} else {
			tempoColeta = "Tempo: " + amostra.getTempoIntervaloColeta() + " " + amostra.getUnidTempoIntervaloColeta().toString();
			
		}
		Pattern pattern ; 
		if (isPatologia){
			pattern = Pattern.compile("[\\s\\S]{1,14}");
		} else {
			pattern = Pattern.compile("[\\s\\S]{1,12}");
		}
		Matcher matcher = pattern.matcher(descMaterial);
		int i = 0;
		while (matcher.find()){
			descMaterialPts[i++] = matcher.group();
		}
		
		//Reduz tamanho da sigla para até 4 caracteres
		String sigla = amostra.getUnidadesFuncionais().getSigla();
		if(sigla.length() > 6) {
			sigla = sigla.substring(0, 6);
		}
		
		//Verifica a existência do número único
		final String numeroUnicoFinal = amostra.getNroUnico() == null ? " " : amostra.getNroUnico().toString();
		
		final StringBuilder textoZpl = new StringBuilder(300);
		
		textoZpl.append(UP_XA).append(NEW_LINE)
		.append(UP_PRA).append(NEW_LINE)
		.append("^PQ01").append(NEW_LINE)
		.append("^FO025,018^ARB,,07^FD").append(sigla).append(UP_FS).append(NEW_LINE)
		.append("^FO025,130^ARB,,07^FD").append(numeroUnicoFinal).append(UP_FS).append(NEW_LINE)
		.append("^FO055,100^ABB,,07^FD").append(DateUtil.dataToString(dataNumeroUnico, "dd/MM/yy")).append(UP_FS).append(NEW_LINE)
		.append("^FO080,025^ACN,,07^FD").append(nomeFinal).append(UP_FS).append(NEW_LINE)
		.append("^FO080,045^ACN,,07^FD").append(prontuarioFinal).append(UP_FS).append(NEW_LINE);
		if (isPatologia){
			textoZpl.append("^FO250,045^ABN,,07^FD").append(local).append(' ').append(salaFinal).append(UP_FS).append(NEW_LINE)
					.append("^FO320,060^ABB,,07^FD").append(tempoColeta).append(UP_FS).append(NEW_LINE)
					.append("^FO350,060^ABB,,07^FD").append(descMaterialPts[0]).append(UP_FS).append(NEW_LINE)
					.append("^FO370,060^ABB,,07^FD").append(descMaterialPts[1]).append(UP_FS).append(NEW_LINE)
					.append("^FO390,060^ABB,,07^FD").append(descMaterialPts[2]).append(UP_FS).append(NEW_LINE)
					.append("^FO410,060^ABB,,07^FD").append(descMaterialPts[3]).append(UP_FS).append(NEW_LINE);
		} else {
			textoZpl.append("^FO280,045^ABN,,07^FD").append(local).append(' ').append(salaFinal).append(UP_FS).append(NEW_LINE)
					.append("^FO350,050^ABB,,07^FD").append(tempoColeta).append(UP_FS).append(NEW_LINE)
					.append("^FO370,050^ABB,,07^FD").append(descMaterialPts[0]).append(UP_FS).append(NEW_LINE)
					.append("^FO390,050^ABB,,07^FD").append(descMaterialPts[1]).append(UP_FS).append(NEW_LINE)
					.append("^FO410,050^ABB,,07^FD").append(descMaterialPts[2]).append(UP_FS).append(NEW_LINE);
		}
		
		textoZpl.append("^FO080,165^ABN,,07^FB340,3,1,L,0^FD").append(exames).append(UP_FS).append(NEW_LINE)
		.append("^FO080,065^BY2,,50^B2N,65,N,N^FD").append(soeSeqFinal).append(amoSeqFinal).append(UP_FS).append(NEW_LINE)
		.append("^FO100,130^ARN,,10^FD").append(soeSeqFinal).append(amoSeqFinal).append(UP_FS).append(NEW_LINE)

		//textoZpl.append("^FO185,025^ABN,,07^FD").append(dataFinal).append(UP_FS).append(NEW_LINE);
				
		
		.append(UP_XZ).append(NEW_LINE)
		.append(UP_XA_IDR_TXT_XZ);

		
		this.geraCtrlEtiquetas(amostra);
		
		return textoZpl.toString();
	}


	public String gerarZPLNumeroAp(final String nroAp) {
	    	final StringBuilder textoZpl = new StringBuilder(50);
		
		textoZpl.append(UP_XA).append(NEW_LINE)
		.append(UP_PRA).append(NEW_LINE)
		.append("^PQ01").append(NEW_LINE)
//		textoZpl.append("^FO025,035^ARB,,07^FD").append(sigla).append(UP_FS).append(NEW_LINE);
//		textoZpl.append("^FO025,130^ATB,,07^FD").append(numeroUnicoFinal).append(UP_FS).append(NEW_LINE);
//		textoZpl.append("^FO065,130^ABB,,07^FD").append(DateUtil.dataToString(dataNumeroUnico, "dd/MM/yy")).append(UP_FS).append(NEW_LINE);
//		textoZpl.append("^FO060,025^ACN,,07^FD").append(nomeFinal).append(UP_FS).append(NEW_LINE);
//		textoZpl.append("^FO185,025^ABN,,07^FD").append(dataFinal).append(UP_FS).append(NEW_LINE);
//		textoZpl.append("^FO360,025^ABN,,07^FD").append(local).append(' ').append(salaFinal).append(UP_FS).append(NEW_LINE);
//		textoZpl.append("^FO060,045^ABN,,07^FB360,3,1,L,0^FD").append(exames).append(UP_FS).append(NEW_LINE);
		.append("^FO060,085^ARN,,10^FD").append(nroAp).append(UP_FS).append(NEW_LINE)
//		textoZpl.append("^FO060,100^ABN,,07^FD").append(tempoColeta).append(UP_FS).append(NEW_LINE);
//		textoZpl.append("^FO120,115^BY2,,50^B2N,65,N,N^FD").append(soeSeqFinal).append(amoSeqFinal).append(UP_FS).append(NEW_LINE);
//		textoZpl.append("^FO130,185^ARN,,10^FD").append(soeSeqFinal).append(amoSeqFinal).append(UP_FS).append(NEW_LINE);
//		textoZpl.append("^FO390,100^ACB,,07^FD").append(prontuarioFinal).append(UP_FS).append(NEW_LINE);
		.append(UP_XZ).append(NEW_LINE)
		.append(UP_XA_IDR_TXT_XZ);
		return textoZpl.toString();
	}
	
	public String gerarZPLEtiquetaNumeroExame(final ImprimeEtiquetaVO imprimeEtiquetaVO) throws BaseException {
		final ImprimeEtiquetaVO etiquetaNumeroExame;
		if (imprimeEtiquetaVO.getSigla() == null || imprimeEtiquetaVO.getNumeroAp() == null) {
			etiquetaNumeroExame = getAelItemSolicitacaoExameDAO().obterSiglaNumeroExameAmostra(imprimeEtiquetaVO);
		} else {
			etiquetaNumeroExame = imprimeEtiquetaVO;
		}

		final StringBuilder etiqueta = new StringBuilder(110);
		String siglaHU = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_PARAMETRO_HU);
		String siglaExame = etiquetaNumeroExame.getSigla();
		String numeroAp = String.valueOf(etiquetaNumeroExame.getNumeroAp());
		String numeroAp2 = numeroAp.substring(0,numeroAp.length()-2) + "/" + numeroAp.substring(numeroAp.length()-2, numeroAp.length());

		etiqueta.append(UP_XA).append(NEW_LINE)
		.append("^LH20,20").append(NEW_LINE)
		.append("^FO45,20^ADN^FH%^FDServi%87o de Patologia - ").append(siglaHU).append(UP_FS).append(NEW_LINE)
		.append("^FO75,45^ATN^FD").append(siglaExame).append(':').append(numeroAp2).append(UP_FS).append(NEW_LINE)	
		.append("^FO75,90^BY2,,65^B3N,N,65,Y,N^FD").append(siglaExame).append(numeroAp).append(UP_FS).append(NEW_LINE)
		//A linha acima imprime o código de barras e o valor do código. Não sendo necessário a linha abaixo.
		//etiqueta.append("^FO150,150^AFN^FD").append(siglaExame).append(numeroAp).append(UP_FS).append(NEW_LINE);	
		.append(UP_XZ).append(NEW_LINE);

		return etiqueta.toString();
	}
	
	/**
	 * ORADB FUNCTION AELC_GERA_CTRL_ETIQ
	 * @param amoSoeSeq
	 * @param Seqp
	 * @param numeroUnico
	 * @param dataNumeroUnico
	 * @param unfSeq
	 */
	private void geraCtrlEtiquetas(final AelAmostras amostra) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Verifica se unidade que executa o exame NÃO tem a característica de gerar número único
		if(!possuiCaracteristica(amostra.getUnidadesFuncionais(), ConstanteAghCaractUnidFuncionais.GERA_NRO_UNICO)){
			return;
		}
		
		final List<AelAmostraCtrlEtiquetas> list = getAelAmostraCtrlEtiquetasDAO().obterAmostraCtrlEtiquetas(amostra.getId().getSoeSeq(),amostra.getId().getSeqp());
		
		AelAmostraCtrlEtiquetas amostraCtrlEtiquetasOld = null;
		
		if(list !=null && !list.isEmpty()){
			amostraCtrlEtiquetasOld = list.get(0);
		}

		if(amostraCtrlEtiquetasOld!=null && amostraCtrlEtiquetasOld.getNroUnico() !=null && amostraCtrlEtiquetasOld.getDtNumeroUnico() !=null 
				&& amostra != null && amostra.getNroUnico() !=null && amostra.getDtNumeroUnico() != null 
				&& amostraCtrlEtiquetasOld.getNroUnico().equals(amostra.getNroUnico()) && amostraCtrlEtiquetasOld.getDtNumeroUnico().equals(amostra.getDtNumeroUnico())){
	
			 final AelAmostraCtrlEtiquetas ctrlEtiquetas = new AelAmostraCtrlEtiquetas();
			 
			 ctrlEtiquetas.setTipoImpressao(DominioTipoImpressaoEtiquetas.R);  
			 ctrlEtiquetas.setNroUnico(amostra.getNroUnico());
			 ctrlEtiquetas.setDtNumeroUnico(amostra.getDtNumeroUnico());
			 ctrlEtiquetas.setCriadoEm(new Date());
			 ctrlEtiquetas.setMachine(null);  // USERENV('TERMINAL')
			 ctrlEtiquetas.setAelAmostras(amostra);
			 ctrlEtiquetas.setRapServidor(servidorLogado);
			
			 getAelAmostraCtrlEtiquetasDAO().persistir(ctrlEtiquetas);
			 getAelAmostraCtrlEtiquetasDAO().flush();
	
		 } else{
			 
			 final AelAmostraCtrlEtiquetas ctrlEtiquetas = new AelAmostraCtrlEtiquetas();
			 
			 ctrlEtiquetas.setTipoImpressao(DominioTipoImpressaoEtiquetas.I);  
			 ctrlEtiquetas.setNroUnico(amostra.getNroUnico());
			 ctrlEtiquetas.setDtNumeroUnico(amostra.getDtNumeroUnico());
			 ctrlEtiquetas.setCriadoEm(new Date());
			 ctrlEtiquetas.setMachine(null);  // USERENV('TERMINAL')
			 ctrlEtiquetas.setAelAmostras(amostra);
			 ctrlEtiquetas.setRapServidor(servidorLogado);
			
			 getAelAmostraCtrlEtiquetasDAO().persistir(ctrlEtiquetas);
			 getAelAmostraCtrlEtiquetasDAO().flush();
			
		 }
		
	}
	
	private boolean possuiCaracteristica(final AghUnidadesFuncionais unf, final ConstanteAghCaractUnidFuncionais caracteristica) {
		
		final AghCaractUnidFuncionais caracteristicaFunc = getAghuFacade().buscarCaracteristicaPorUnidadeCaracteristica(unf.getSeq(), caracteristica);
		
		return caracteristicaFunc != null;
	}
	

	/**
	 * Retorna nomes de pessoas somente com a primeira letra de cada nome
	 * 
	 * ORADB FUNCTION AELC_FORMATA_NOME
	 * 
	 */
	public String formatarNome(final String nome) {
		String nomeFormatado = nome.toUpperCase().trim();
		
		//Remove 'conectores'
		nomeFormatado = nomeFormatado.replaceAll(" DA ", " ");
		nomeFormatado = nomeFormatado.replaceAll(" DE ", " ");
		nomeFormatado = nomeFormatado.replaceAll(" DI ", " ");
		nomeFormatado = nomeFormatado.replaceAll(" DO ", " ");
		nomeFormatado = nomeFormatado.replaceAll(" DOS ", " ");
		nomeFormatado = nomeFormatado.replaceAll(" DAS ", " ");
		
		//Remove múltiplos espaços entre os nomes
		nomeFormatado = nomeFormatado.replaceAll("\\b\\s{2,}\\b", " ");
		
		//Trata para nome de recém-nascidos
		String inicialRN = "";
		if(nomeFormatado.startsWith("RN")) {
			if(!nomeFormatado.startsWith("RN ")) { //Se o próximo caracter não for espaço
				inicialRN = nomeFormatado.substring(0, nomeFormatado.indexOf(' ')) + " ";
				nomeFormatado = nomeFormatado.substring(nomeFormatado.indexOf(' ') + 1); // Tudo a partir do 1 espaço
			} else {
				final String[] tokensNome = nomeFormatado.split(" ");
				if(tokensNome.length > 2 && Arrays.asList(NUMEROS_ROMANOS_ACEITOS).contains(tokensNome[1])) { //Se o segundo token for um número romano
					final int posSegundoEspaco = nomeFormatado.indexOf(" ", nomeFormatado.indexOf(' ') + 1);
					if(posSegundoEspaco != -1) {
						inicialRN = nomeFormatado.substring(0, posSegundoEspaco) + " ";
						nomeFormatado = nomeFormatado.substring(posSegundoEspaco + 1); // Tudo a partir do 2 espaço
					}
				} else {
					inicialRN = nomeFormatado.substring(0, nomeFormatado.indexOf(' ')) + " ";
					nomeFormatado = nomeFormatado.substring(nomeFormatado.indexOf(' ') + 1); // Tudo a partir do 1 espaço
				}
			}
		}
		
		final StringBuilder iniciais = new StringBuilder();
		
		try {
			for(final String parteNome : nomeFormatado.split(" ")) {
				if(parteNome.length() > 0) {
					iniciais.append(parteNome.charAt(0));
				}
			}
		}catch (final StringIndexOutOfBoundsException e) {
			LOG.error(e.getMessage(), e);
		}
		
		return inicialRN + iniciais.toString();
	}
	
	public String reimprimirAmostra(AghUnidadesFuncionais unidadeExecutora, final Integer soeSeq, final Short seqp, final String nomeMicro, final String nomeImpressora) throws BaseException {
				if (nomeMicro == null && nomeImpressora == null) {
					LOG.error("nomeMicro e nomeImpressora não podem ambos estar nulos.");
				}
				String impressora;
				final AelSolicitacaoExames solicitacaoExame = this.getExamesFacade().obterAelSolicitacaoExamePorChavePrimaria(soeSeq);
				// Mesmo que seja exame REDOME, se tiver alguma informação indisponível então imprime etiqueta normal
				if (etiquetasRedomeON.verificarSeImprimeEtiquetaComoRedome(unidadeExecutora, solicitacaoExame, seqp)) {
					impressora = (nomeImpressora == null) ? etiquetasRedomeON.obterNomeImpressoraEtiquetasRedome(nomeMicro) : nomeImpressora;
					etiquetasRedomeON.reimprimirAmostraRedome(unidadeExecutora, soeSeq, seqp, impressora);
				} else {
					impressora = (nomeImpressora == null) ? obterNomeImpressoraEtiquetas(nomeMicro) : nomeImpressora;
					reimprimirAmostraNormal(unidadeExecutora, soeSeq, seqp, impressora);
				}
				return impressora;
	}
	
	/**
	 * @throws BaseException 
	 * @ORADB EVT_WHEN_BUTTON_PRESSED
	 * 
	 **/
	public void reimprimirAmostraNormal(AghUnidadesFuncionais unidadeExecutora, final Integer soeSeq, final Short seqp, final String nomeImpressora) throws BaseException {
		if (nomeImpressora == null) { 
			throw new ApplicationBusinessException(EtiquetasONExceptionCode.ERRO_IMPRESSORA_ETIQUETA_NAO_ENCONTRADA, Severity.INFO);
		}
		
		unidadeExecutora = aghuFacade.obterAghUnidFuncionaisPeloId(unidadeExecutora.getSeq());
		
		if (isVerificaUnidade(unidadeExecutora, ConstanteAghCaractUnidFuncionais.UNID_COLETA)) {
			if (seqp == null) { // imprimir todas

				final List<AelAmostras> amostras = this.getExamesFacade().buscarAmostrasPorSolicitacaoExame(soeSeq, null);
				for (final AelAmostras amostra : amostras) {
					if (DominioSituacaoAmostra.G.equals(amostra.getSituacao()) || DominioSituacaoAmostra.C.equals(amostra.getSituacao())
							|| DominioSituacaoAmostra.U.equals(amostra.getSituacao()) || DominioSituacaoAmostra.M.equals(amostra.getSituacao())) {
						this.gerarEtiquetas(amostra.getSolicitacaoExame(), unidadeExecutora, nomeImpressora, null,false);
						break;
					}
				}
			} else { // imprimir apenas uma
				final AelAmostras amostra = this.getExamesFacade().buscarAmostrasPorId(soeSeq, seqp);
				if (DominioSituacaoAmostra.G.equals(amostra.getSituacao()) || DominioSituacaoAmostra.C.equals(amostra.getSituacao())
						|| DominioSituacaoAmostra.U.equals(amostra.getSituacao()) || DominioSituacaoAmostra.M.equals(amostra.getSituacao())) {
					this.gerarEtiquetas(amostra.getSolicitacaoExame(), amostra.getId().getSeqp(), unidadeExecutora, nomeImpressora, null,false);
				}
			}
		} else {
			final AelSolicitacaoExames solicitacaoExame = this.getExamesFacade().obterAelSolicitacaoExamePorChavePrimaria(soeSeq);
			this.gerarEtiquetas(solicitacaoExame, seqp, unidadeExecutora, nomeImpressora, null,false);
		}
	}

	/* Verifica se a unidade executora de exames é uma UNIDADE PATOLOGICA */
	private boolean isVerificaUnidade(final AghUnidadesFuncionais unidadeExecutora, final ConstanteAghCaractUnidFuncionais caracteristica) {

		if (unidadeExecutora != null) {
			return aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeExecutora.getSeq(),caracteristica);
		}
		return false;
	}
	
	/**
	 * Gera o código ZPL para impressão do envelope do paciente
	 * 
	 * @param nome
	 * @param solicitacao
	 * @param unidadeExecutora
	 * @param data
	 * @param prontuario
	 * @return
	 */
	public String gerarEtiquetaEnvelopePaciente(String nome, Integer solicitacao, String unidadeExecutora, String data, Integer prontuario) {
		StringBuffer zpl = new StringBuffer(350);
	
		String nomePaciente = nome.replaceAll("[^\\p{ASCII}]", "");  
		String unidadeExecutoraF = unidadeExecutora.replaceAll("[^\\p{ASCII}]", "");
		String prontuarioFormatado = CoreUtil.formataProntuarioRelatorio(prontuario);

		zpl.append(UP_XA).append(NEW_LINE)
		.append("^LL780").append(NEW_LINE)
		.append("^FX IMPRIMIR ETIQUETA DO ENVELOPE DO PACIENTE ^FS").append(NEW_LINE)
		.append("^FO28,26^AEN,26,13^FDNome:^FS").append(NEW_LINE)
		.append("^FO147,28^FB680,2,0,L,0^AFN,26,13^FH^FD").append(nomePaciente).append(UP_FS).append(NEW_LINE)
		.append("^FO28,91^AFN,26,13^FH^FDN_A3mero da Solicita_87_84o: ").append(solicitacao).append(UP_FS).append(NEW_LINE)
		.append("^FO28,121^FB780,2,0,L,0^AFN,26,13^FDUnidade Executora: ").append(unidadeExecutoraF).append(UP_FS).append(NEW_LINE)
		.append("^FO28,188^AFN,26,13^FDData: ").append(data).append(UP_FS).append(NEW_LINE)
		.append("^FO400,240^AEN,26,13^FH^FDProntu_A0rio:^FS").append(NEW_LINE)
		.append("^FO640,242^AFN,26,13^FH^FD").append(prontuarioFormatado).append(UP_FS)
		.append(UP_XZ).append(NEW_LINE);

		return zpl.toString();

	}
	
	/**
	 * Melhoria #34778: Habilitar a impressão de etiquetas de amostra de exame coletado pelo solicitante
	 * 
	 * ORADB: AELP_CHAMA_ETIQ_BARRAS (procedure)
	 * 
	 * Obs.: a outra parte migrada desta procedure migrada encontra-se na SolicitacaoExamesON.
	 * A implementação foi separada devido a necessidade de primeiro verificar se deverá solicitar a confirmação
	 * da impressao das etiquetas de amostra (no caso em que esta for coletada pelo solicitante) conforme regras
	 * implementadas neste método. No método da SolicitacaoExamesON irá chamar diretamente a impressão da etiqueta.
	 * 
	 * Retorna um VO indicando se as etiquetas devem ser impressas e se as mesmas devem
	 * ser impressas somente para os itens de exame em uma determinada situação.
	 * 
	 * @return ConfirmacaoImpressaoEtiquetaVO
	 */
	public ConfirmacaoImpressaoEtiquetaVO verificarImpressaoEtiqueta(
			final SolicitacaoExameVO solicitacaoExameVO,
			final AghMicrocomputador microcomputador) throws ApplicationBusinessException {
				
		AghParametros paramUnidImuno = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_UNID_IMUNO);
		
		//		Se a unidade do paciente "ISE.UFE_UNF_SEQ" for iqual a P_UNID_IMUNO
		//		Então verificar se exame Redome . 
		List<AelItemSolicitacaoExames> listaItemSolicitacaoExame = aelItemSolicitacaoExameDAO
				.pesquisarItensSolicitacoesExamesPorSolicitacao(solicitacaoExameVO.getSeqSolicitacaoSalva());
		AelItemSolicitacaoExames itemSolicitacaoExame = listaItemSolicitacaoExame.get(0);
		AghUnidadesFuncionais unidFuncional = itemSolicitacaoExame.getUnidadeFuncional();
		
		if (unidFuncional.getSeq().shortValue() == paramUnidImuno.getVlrNumerico().shortValueExact()) {
			AghParametros paramGrupoPesqExamesRedome = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_GRUPO_PESQUISA_DESCRICAO_REDOME);
			AelAgrpPesquisas agrpPesquisa = aelAgrpPesquisasDAO.obterAelAgrpPesquisasPorDescricao(paramGrupoPesqExamesRedome.getVlrTexto());
			// Verifica se é um exame de Redome, se for de Redome não deve ser impresso estas Etiquetas 
			if (agrpPesquisa != null) {
				AelUnfExecutaExames unfExecutaExame = itemSolicitacaoExame.getAelUnfExecutaExames();
				AelUnfExecutaExamesId unfExecutaExameId = unfExecutaExame.getId();
				// 1.2 - Seleciona exames do Grupo pesquisa Redome se vier algum registro retorna False 
				AelAgrpPesquisaXExame agrpPesquisaXExame = aelAgrpPesquisaXExameDAO.buscarAtivoPorUnfExecutaExame(
						unfExecutaExameId.getEmaExaSigla(),
						unfExecutaExameId.getEmaManSeq(),
						unfExecutaExameId.getUnfSeq().getSeq(), null);
				
				if (agrpPesquisaXExame != null) {
					return new ConfirmacaoImpressaoEtiquetaVO(Boolean.FALSE, null, Boolean.FALSE);
				}
			}
		}
			
		AghUnidadesFuncionais unidFuncionalSOE = solicitacaoExameVO.getModel().getUnidadeFuncional();
		List<AghCaractUnidFuncionais> caracteristicas = aghuFacade.listarCaracteristicasUnidadesFuncionais(unidFuncionalSOE.getSeq() , null, null, null);
		
		DominioOrigemAtendimento origemAtendimento = solicitacaoExameVO.getAtendimento().getOrigem();
		
		Boolean temCaracImprimeEtiquetasAmostra = Boolean.FALSE;
		
		for (AghCaractUnidFuncionais acuf : caracteristicas) {			
			if (acuf != null && acuf.getId() != null && ConstanteAghCaractUnidFuncionais.IMPRIME_ETIQUETAS_AMOSTRA.equals(acuf.getId().getCaracteristica())) {
				temCaracImprimeEtiquetasAmostra = Boolean.TRUE;
				break;
			}
		}
		
		if(!temCaracImprimeEtiquetasAmostra){
			return new ConfirmacaoImpressaoEtiquetaVO(Boolean.FALSE, null, Boolean.FALSE);
		}
		
		for (AghCaractUnidFuncionais acuf : caracteristicas) {
			if(acuf!=null && acuf.getId()!=null && acuf.getId().getCaracteristica()!=null){
				
				if(acuf.getId().getCaracteristica().equals(ConstanteAghCaractUnidFuncionais.UNID_CONVENIO)){
					return new ConfirmacaoImpressaoEtiquetaVO(Boolean.FALSE, null, Boolean.TRUE);
				}
				
				if(acuf.getId().getCaracteristica().equals(ConstanteAghCaractUnidFuncionais.UNID_COLETA)){
					if (origemAtendimento.toString().equals(
							Origens.A.toString())
							|| origemAtendimento.toString().equals(Origens.X.toString())
							|| origemAtendimento.toString().equals(Origens.C.toString())) {
						return new ConfirmacaoImpressaoEtiquetaVO(Boolean.FALSE, null, Boolean.TRUE);
					}else{
						return new ConfirmacaoImpressaoEtiquetaVO(Boolean.FALSE, null, Boolean.FALSE);
					}
				}
			}
		}
			
		AelSolicitacaoExames solicitacaoExame = itemSolicitacaoExame.getSolicitacaoExame();
		AelUnfExecutaExames unfExecutaExame = itemSolicitacaoExame.getAelUnfExecutaExames();	
		
		UnfExecutaSinonimoExameVO unfExecutaSinonimoExameVO = new UnfExecutaSinonimoExameVO(unfExecutaExame);
		ItemSolicitacaoExameVO itemSolicitacaoExameVO = new ItemSolicitacaoExameVO();
		itemSolicitacaoExameVO.setUnfExecutaExame(unfExecutaSinonimoExameVO);
		itemSolicitacaoExameVO.setSolicitacaoExameVO(solicitacaoExameVO);
		
		DominioResponsavelColetaExames responsavelColetaExame = itemSolicitacaoExameON
				.obterResponsavelColeta(
						solicitacaoExame.getAtendimento(),
						solicitacaoExame.getAtendimentoDiverso(),
						itemSolicitacaoExameVO);
		
		AghParametros paramSituacaoColetadoSolic = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_COLETADO_SOLIC);
		AghParametros paramSituacaoAColetar = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
		
		return obterRetornoConfirmaImpressaoEtiqueta(microcomputador,
				listaItemSolicitacaoExame, unidFuncional,
				responsavelColetaExame, paramSituacaoColetadoSolic,
				paramSituacaoAColetar);
	}

	private ConfirmacaoImpressaoEtiquetaVO obterRetornoConfirmaImpressaoEtiqueta(
			final AghMicrocomputador microcomputador,
			List<AelItemSolicitacaoExames> listaItemSolicitacaoExame,
			AghUnidadesFuncionais unidFuncional,
			DominioResponsavelColetaExames responsavelColetaExame,
			AghParametros paramSituacaoColetadoSolic,
			AghParametros paramSituacaoAColetar)
			throws ApplicationBusinessException {
		AelSitItemSolicitacoes sitItemSolicColetadoSolic = aelSitItemSolicitacoesDAO.obterPeloId(paramSituacaoColetadoSolic.getVlrTexto());
		AelSitItemSolicitacoes sitItemSolicAColetar = aelSitItemSolicitacoesDAO.obterPeloId(paramSituacaoAColetar.getVlrTexto());
		
		Boolean temSituacaoColetadoSolic = Boolean.FALSE;
		Boolean temSituacaoAColetar = Boolean.FALSE;
		
		if (!listaItemSolicitacaoExame.isEmpty()) {
			for (AelItemSolicitacaoExames itemSolic : listaItemSolicitacaoExame) {
				if (itemSolic.getSituacaoItemSolicitacao().equals(sitItemSolicColetadoSolic)) {
					// Situação é COLETADO PELO SOLICITANTE ('CS')
					temSituacaoColetadoSolic = Boolean.TRUE;
					break;
				}
			}
			
			for (AelItemSolicitacaoExames itemSolicit : listaItemSolicitacaoExame) {
				if (itemSolicit.getSituacaoItemSolicitacao().equals(sitItemSolicAColetar)) {
					// Situação é A COLETAR ('AC')
					temSituacaoAColetar = Boolean.TRUE;
					break;
				}
			}
		}

		AghUnidadesFuncionais unidFuncionalMicro = microcomputador.getAghUnidadesFuncionais();
		Short unfSeqMicro = null;
		if (unidFuncionalMicro != null) {
			unfSeqMicro = unidFuncionalMicro.getSeq();
		}
		AghParametros paramUnfSeqDialise = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_UNF_SEQ_DIALISE);
		
		if (temSituacaoColetadoSolic) {
			// Imprime etiqueta somente exames CS-Coletado pelo Solicitante 
			return new ConfirmacaoImpressaoEtiquetaVO(Boolean.TRUE, sitItemSolicColetadoSolic.getCodigo());
			
		} else if ((unidFuncional.possuiCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_CTI) && DominioResponsavelColetaExames.P.equals(responsavelColetaExame))
				|| (unfSeqMicro != null && unfSeqMicro.equals(paramUnfSeqDialise.getVlrNumerico()) && temSituacaoAColetar)) {
			// Imprime etiquetas de Exames:  Unidade CTI e Responsabilidade pela Coleta = Paciente ou se unidade do micro for Dialise e situação = 'AC
			return new ConfirmacaoImpressaoEtiquetaVO(Boolean.TRUE, null, Boolean.FALSE);
			
		} else {
			return new ConfirmacaoImpressaoEtiquetaVO(Boolean.FALSE, null, Boolean.FALSE);
		}
	}
	
	public String obterNomeImpressoraEtiquetas(String nomeMicro) {
		AghMicrocomputador microcomputador = administracaoFacade.obterAghMicroComputadorPorNomeOuIP(nomeMicro, null);
		if (microcomputador != null && microcomputador.getImpressoraEtiquetas() != null) {
			return microcomputador.getImpressoraEtiquetas();
		}
		if (microcomputador != null && microcomputador.getAghUnidadesFuncionais() != null) {
			List<AghImpressoraPadraoUnids> lista = aghuFacade.listarAghImpressoraPadraoUnids(microcomputador
					.getAghUnidadesFuncionais().getSeq(), TipoDocumentoImpressao.ETIQUETAS_BARRAS);
			if (!lista.isEmpty()) {
				if (lista.get(0).getImpImpressora() != null) {
					return lista.get(0).getImpImpressora().getFilaImpressora();
				}
				if (lista.get(0).getNomeImpressora() != null) {
					return lista.get(0).getNomeImpressora();
				}
			}
		}
		return null;
	}
	
	public ImpImpressora obterImpressoraEtiquetas(String nomeMicro) {
		AghMicrocomputador microcomputador = administracaoFacade.obterAghMicroComputadorPorNomeOuIP(nomeMicro, null);

		if (microcomputador != null && microcomputador.getAghUnidadesFuncionais() != null) {
			List<AghImpressoraPadraoUnids> lista = aghuFacade.listarAghImpressoraPadraoUnids(microcomputador
					.getAghUnidadesFuncionais().getSeq(), TipoDocumentoImpressao.ETIQUETAS_BARRAS);
			if (!lista.isEmpty()) {
				if (lista.get(0).getImpImpressora() != null) {
					return lista.get(0).getImpImpressora();
				}
			}
		}
		return null;
	}
	
	private ItemSolicitacaoExameRN getItemSolicitacaoExameRN() {
		return itemSolicitacaoExameRN;
	}
	
	private AelAmostraItemExamesRN getAelAmostraItemExamesRN() {
		return aelAmostraItemExamesRN;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected AelAmostrasDAO getAelAmostrasDAO() {
		return aelAmostrasDAO;
	}
	
	protected AelAmostrasRN getAelAmostrasRN() {
		return aelAmostrasRN;
	}
	
	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}
	
	protected AelItemHorarioAgendadoDAO getAelItemHorarioAgendadoDAO() {
		return aelItemHorarioAgendadoDAO;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}

	protected AelAmostraCtrlEtiquetasDAO getAelAmostraCtrlEtiquetasDAO() {
		return aelAmostraCtrlEtiquetasDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected AelTipoAmostraExameDAO getAelTipoAmostraExameDAO() {
		return aelTipoAmostraExameDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return this.aelItemSolicitacaoExameDAO;
	}	
}
