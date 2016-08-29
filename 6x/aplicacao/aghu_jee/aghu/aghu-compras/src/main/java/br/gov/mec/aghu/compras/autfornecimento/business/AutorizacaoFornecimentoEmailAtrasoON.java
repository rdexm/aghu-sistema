package br.gov.mec.aghu.compras.autfornecimento.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Days;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoEmailAtrasoVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoEmailVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.AutorizacaoFornecimentoItemFornecedorVO;
import br.gov.mec.aghu.compras.dao.ScoContatoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoPedidoMatExpedienteDAO;
import br.gov.mec.aghu.compras.vo.ParcelasAFPendEntVO;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class AutorizacaoFornecimentoEmailAtrasoON extends BaseBusiness {

    private static final String BR = "<br>";

    private static final String NLNL = "\n\n";

    private static final String END_TABLE_CELL_TAG = "</td>";

    private static final String TABLE_CELL_TAG = "<td>";

    private static final long serialVersionUID = -2450738996666609590L;

    private static final Boolean PARCELAS_ATRASADAS = true;
    private static final Boolean PARCELAS_NAO_ATRASADAS = false;
    private static final Boolean GERAR_HTML = true;
    private static final Boolean NAO_GERAR_HTML = false;

    private static final Log LOG = LogFactory.getLog(AutorizacaoFornecimentoEmailAtrasoON.class);

    @EJB
    private IServidorLogadoFacade servidorLogadoFacade;

    @Inject
    private ScoPedidoMatExpedienteDAO scoPedidoMatExpedienteDAO;

    @Inject
    private ScoContatoFornecedorDAO scoContatoFornecedorDAO;

    @EJB
    public IParametroFacade parametroFacade;

    @EJB
    public IRegistroColaboradorFacade registroColaboradorFacade;

    @EJB
    private AutorizacaoFornecimentoEmailAtrasoRN autorizacaoFornecimentoEmailAtrasoRN;

    public enum AutorizacaoFornecimentoEmailAtrasoONMessageCode implements BusinessExceptionCode {
	SELECIONAR_PELO_MENOS_UMA_AF, FORNECEDORES_SEM_CONTATO_EMAIL, AFP_EM_DIFERENTE_SITUACAO, EMAIL_ENVIADO_SUCESSO, EMAIL_SELECIONE_NOTIFICACAO, USUARIO_SEM_END_EMAIL;
    }

    public String determinarProcessamentoAFs(AutorizacaoFornecimentoEmailAtrasoVO autorizacaoFornecimentoEmailAtrasoVO,
	    List<ParcelasAFPendEntVO> afsSelecionadas) throws ApplicationBusinessException {
	RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
	if (StringUtils.isBlank(servidorLogado.getEmail())) {
	    autorizacaoFornecimentoEmailAtrasoVO.setExibirModal(Boolean.FALSE);
	    throw new ApplicationBusinessException(AutorizacaoFornecimentoEmailAtrasoONMessageCode.USUARIO_SEM_END_EMAIL);
	}

	if (afsSelecionadas == null || afsSelecionadas.size() == 0) {
	    autorizacaoFornecimentoEmailAtrasoVO.setExibirModal(Boolean.FALSE);
	    throw new ApplicationBusinessException(AutorizacaoFornecimentoEmailAtrasoONMessageCode.SELECIONAR_PELO_MENOS_UMA_AF);
	}
	if (isAFsParcelasAtrasoNaoAtraso(afsSelecionadas)) {
	    autorizacaoFornecimentoEmailAtrasoVO.setExibirModal(Boolean.FALSE);
	    throw new ApplicationBusinessException(AutorizacaoFornecimentoEmailAtrasoONMessageCode.AFP_EM_DIFERENTE_SITUACAO);
	}

	if (isTodosFornecedoresSemEmail(afsSelecionadas)) {
	    return null;
	}

	if (verificarMesmoFornecedorAFs(afsSelecionadas)) {
	    return "autorizacaoFornecimentoPendenteEntregaEnviarEmailAtraso";
	}
	autorizacaoFornecimentoEmailAtrasoVO.setExibirModal(Boolean.TRUE);
	return null;
    }

    /**
     * Envia email para mais de um fornecedor #24965
     * 
     * @param afsSelecionadas
     *            AFs selecionadas
     * @throws AGHUNegocioException
     */
    public void enviarEmailMultiplosFornecedores(List<ParcelasAFPendEntVO> afsSelecionadas) throws ApplicationBusinessException {
	// criar listas separadas por fornecedor
	List<ScoFornecedor> fornecedoresSemEmail = null;
	Map<ParcelasAFPendEntVO, List<ParcelasAFPendEntVO>> afsPorFornecedor = separarFornecedores(afsSelecionadas);
	// enviar email multiplos fornecedores
	for (Iterator<ParcelasAFPendEntVO> i = afsPorFornecedor.keySet().iterator(); i.hasNext();) {
	    ParcelasAFPendEntVO fornecedor = i.next();

	    if (fornecedor.getContato() != null && fornecedor.getContato().getEMail() != null && !fornecedor.getContato().getEMail().isEmpty()) {
		AutorizacaoFornecimentoEmailVO dadosEmail = montarCorpoEmail(afsPorFornecedor.get(fornecedor));
		dadosEmail.setEmailFornecedor(fornecedor.getContato().getEMail());
		dadosEmail.setEmailUsuarioLogado(this.getServidorLogadoFacade().obterServidorLogado().getEmail());
		dadosEmail.setEnviarFornecedor(true);
		dadosEmail.setEnviarUsuarioLogado(true);
		dadosEmail.setMostrarMensagem(false);
		getAutorizacaoFornecimentoEmailAtrasoRN().enviarEmailFornecedor(dadosEmail);
	    } else {
		// logar mensagem com dados de fornecedores sem email
		if (fornecedoresSemEmail == null) {
		    fornecedoresSemEmail = new ArrayList<ScoFornecedor>();
		}
		fornecedoresSemEmail.add(fornecedor.getFornecedor());
	    }
	}

	notificarFornecedoresSemEmail(fornecedoresSemEmail);
    }

    private String notificarFornecedoresSemEmail(List<ScoFornecedor> fornecedoresSemEmail) throws ApplicationBusinessException {
	if (fornecedoresSemEmail != null && fornecedoresSemEmail.size() > 0) {
	    StringBuilder fornecedores = new StringBuilder();
	    for (ScoFornecedor scoFornecedor : fornecedoresSemEmail) {
		fornecedores.append(scoFornecedor.getRazaoSocial()).append('\n');
	    }
	    throw new ApplicationBusinessException(AutorizacaoFornecimentoEmailAtrasoONMessageCode.FORNECEDORES_SEM_CONTATO_EMAIL,
		    Severity.ERROR, fornecedores.toString());
	} else {
	    return null;
	}
    }

    private Map<ParcelasAFPendEntVO, List<ParcelasAFPendEntVO>> separarFornecedores(List<ParcelasAFPendEntVO> afsSelecionadas) {
	ParcelasAFPendEntVO fornecedorAnterior = afsSelecionadas.get(0);
	Map<ParcelasAFPendEntVO, List<ParcelasAFPendEntVO>> afsPorFornecedor = new HashMap<ParcelasAFPendEntVO, List<ParcelasAFPendEntVO>>();
	List<ParcelasAFPendEntVO> afsFornecedor = new ArrayList<ParcelasAFPendEntVO>();
	for (ParcelasAFPendEntVO parcelasAFPendEntVO : afsSelecionadas) {
	    if (fornecedorAnterior.getFornecedor().equals(parcelasAFPendEntVO.getFornecedor())) {
		afsFornecedor.add(parcelasAFPendEntVO);
	    } else {
		afsPorFornecedor.put(fornecedorAnterior, afsFornecedor);
		afsFornecedor = new ArrayList<ParcelasAFPendEntVO>();
		fornecedorAnterior = parcelasAFPendEntVO;
		afsFornecedor.add(parcelasAFPendEntVO);
	    }
	}
	afsPorFornecedor.put(fornecedorAnterior, afsFornecedor);
	return afsPorFornecedor;
    }

    private boolean verificarMesmoFornecedorAFs(List<ParcelasAFPendEntVO> afsSelecionadas) {
	boolean isMesmoFornecedor = true;
	ScoFornecedor scoFornecedorAnterior = afsSelecionadas.get(0).getFornecedor();
	for (ParcelasAFPendEntVO parcelasAFPendEntVO : afsSelecionadas) {
	    if (!scoFornecedorAnterior.equals(parcelasAFPendEntVO.getFornecedor())) {
		isMesmoFornecedor = false;
		break;
	    }
	    scoFornecedorAnterior = parcelasAFPendEntVO.getFornecedor();
	}

	return isMesmoFornecedor;
    }

    private boolean isAFsParcelasAtrasoNaoAtraso(List<ParcelasAFPendEntVO> afsSelecionadas) {
	int totalAFPsComAtraso = 0;
	int totalAFPsSemAtraso = 0;
	for (ParcelasAFPendEntVO parcelasAFPendEntVO : afsSelecionadas) {
	    if (parcelasAFPendEntVO.isAtrasado()) {
		totalAFPsComAtraso++;
	    } else {
		totalAFPsSemAtraso++;
	    }
	    if (totalAFPsComAtraso > 0 && totalAFPsSemAtraso > 0) {
		return true;
	    }
	}
	return false;
    }

    private boolean isAFsAtrasadas(List<ParcelasAFPendEntVO> afsSelecionadas) {
	for (ParcelasAFPendEntVO parcelasAFPendEntVO : afsSelecionadas) {
	    if (parcelasAFPendEntVO.isAtrasado()) {
		return true;
	    }
	    return false;
	}
	return false;
    }

    /**
     * Carregar informacoes para label da tela de envio de email de parcelas
     * atrasadas #24965
     * 
     * @param servidorLogado
     *            usuario logado
     * @param numeroAutorizacaoFornecedor
     *            numero de autorizacao do fornecedor
     * @return AutorizacaoFornecimentoEmailAtrasoVO com labels para tela
     */
    public AutorizacaoFornecimentoEmailAtrasoVO carregarLabelsParaTelaAutFornEmailAtraso(Integer numeroAutorizacaoFornecedor) {
	RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();

	AutorizacaoFornecimentoEmailAtrasoVO autorizacaoFornecimentoEmailAtrasoVO = new AutorizacaoFornecimentoEmailAtrasoVO();

	ScoContatoFornecedor scoContaFornecedor = getScoContatoFornecedorDAO().obterFornecedorPorNumeroAutorizacaoFornecedor(
		numeroAutorizacaoFornecedor);
	autorizacaoFornecimentoEmailAtrasoVO.setContatoFornecedor(scoContaFornecedor);
	String labelEnviarFornecedor = getResourceBundleValue("LABEL_ENVIAR_FORNECEDOR_EMAIL_ATRASO", scoContaFornecedor.getEMail());
	autorizacaoFornecimentoEmailAtrasoVO.setLabelEnviarFornecedor(labelEnviarFornecedor);

	String labelEnviarUsuarioLogado = getResourceBundleValue("LABEL_ENVIAR_USUARIO_LOGADO_EMAIL_ATRASO", servidorLogado.getEmail());
	autorizacaoFornecimentoEmailAtrasoVO.setLabelEnviarUsuarioLogado(labelEnviarUsuarioLogado);

	String labelEnviarCopia = getResourceBundleValue("LABEL_ENVIAR_COPIA_EMAIL_ATRASO");
	autorizacaoFornecimentoEmailAtrasoVO.setLabelEnviarCopia(labelEnviarCopia);

	String labelAnexarPlanilha = getResourceBundleValue("LABEL_ANEXAR_PLANILHA_EMAIL_ATRASO");
	autorizacaoFornecimentoEmailAtrasoVO.setLabelAnexarPlanilha(labelAnexarPlanilha);

	return autorizacaoFornecimentoEmailAtrasoVO;
    }

    /**
     * Monta o corpo do email de notificacao da tela
     * autorizacaoFornecimentoPendenteEntrega
     * 
     * #24965
     * 
     * @param afsSelecionadas
     *            AFs selecionadas para notificao
     * @return AutorizacaoFornecimentoEmailVO
     * @throws AGHUNegocioException
     */
    public AutorizacaoFornecimentoEmailVO montarCorpoEmail(List<ParcelasAFPendEntVO> afsSelecionadas) throws ApplicationBusinessException {
	Integer numeroAutorizacaoFornecedor = afsSelecionadas.get(0).getNumeroAF();

	List<AutorizacaoFornecimentoItemFornecedorVO> listaMateriaisEmail = getAutorizacaoFornecimentoEmailAtrasoRN()
		.listarAFsMateriaisParaEmailAtraso(afsSelecionadas);

	AutorizacaoFornecimentoEmailVO autorizacaoFornecimentoEmailVO = new AutorizacaoFornecimentoEmailVO();

	autorizacaoFornecimentoEmailVO.setItensFornecedor(listaMateriaisEmail);

	String textoEmail = "";
	String textoEmailHtml = "";
	String emailAssunto = "";
	if (isAFsAtrasadas(afsSelecionadas)) {
	    textoEmail = montarEmailAFsAtrasadas(afsSelecionadas, numeroAutorizacaoFornecedor, NAO_GERAR_HTML, PARCELAS_ATRASADAS);
	    textoEmailHtml = montarEmailAFsAtrasadas(afsSelecionadas, numeroAutorizacaoFornecedor, GERAR_HTML, PARCELAS_ATRASADAS);
	    emailAssunto = getResourceBundleValue("EMAIL_ASSUNTO_AFS_COM_ATRASO");
	} else {
	    textoEmail = montarEmailAFsSemAtraso(afsSelecionadas, numeroAutorizacaoFornecedor, NAO_GERAR_HTML, PARCELAS_NAO_ATRASADAS);
	    textoEmailHtml = montarEmailAFsSemAtraso(afsSelecionadas, numeroAutorizacaoFornecedor, GERAR_HTML, PARCELAS_NAO_ATRASADAS);
	    emailAssunto = getResourceBundleValue("EMAIL_ASSUNTO_AFS_SEM_ATRASO");
	}
	autorizacaoFornecimentoEmailVO.setTextoEmail(textoEmail);
	autorizacaoFornecimentoEmailVO.setTextoEmailHTML(textoEmailHtml);
	autorizacaoFornecimentoEmailVO.setEmailAssunto(emailAssunto);
	autorizacaoFornecimentoEmailVO.setPlanilhaAnexo(montarItensFornecedorCorporEmailCSV(afsSelecionadas).getBytes());
	autorizacaoFornecimentoEmailVO.setRazaoSocialFornecedor(afsSelecionadas.get(0).getFornecedor().getRazaoSocial());

	return autorizacaoFornecimentoEmailVO;
    }

    private String montarEmailAFsSemAtraso(List<ParcelasAFPendEntVO> afsSelecionadas, Integer numeroAutorizacaoFornecedor,
	    boolean gerarHtml, boolean isParcelasAtrasadas) throws ApplicationBusinessException {
	// montar email AFs nao atrasadas
	// C1
	ScoFornecedor fornecedor = afsSelecionadas.get(0).getFornecedor();
	// getScoFornecedorDAO().obterFornecedorPorAF(numeroAutorizacaoFornecedor);
	String razaoSocialFornecedor = getResourceBundleValue("EMAIL_PARCELA_ATRASADA_FORNECEDOR");
	if (!gerarHtml) {
	    razaoSocialFornecedor = razaoSocialFornecedor.replaceAll(BR, "n");
	}
	razaoSocialFornecedor = getResourceBundleValue(razaoSocialFornecedor, fornecedor.getRazaoSocial());

	String emailLembrete = getResourceBundleValue("EMAIL_AFS_SEM_ATRASO_LEMBRETE");
	String urlHospital = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_SITE);
	String emailVisualizar = getResourceBundleValue("EMAIL_AFS_SEM_ATRASO_VISUALIZAR", urlHospital.toLowerCase());

	if (!gerarHtml) {
	    emailVisualizar = emailVisualizar.replaceAll(BR, "n");
	}

	RapPessoasFisicas chefeServicoCompras = getRegistroColaboradorFacade().obterChefeServicoComprasPorAghParametros();
	String chefeServicoComprasFormatado = getResourceBundleValue("EMAIL_PARCELA_ATRASADA_CHEFE_COMPRAS");
	if (!gerarHtml) {
	    chefeServicoComprasFormatado = chefeServicoComprasFormatado.replaceAll(BR, "n");
	}
	chefeServicoComprasFormatado = getResourceBundleValue(chefeServicoComprasFormatado, chefeServicoCompras.getNome());

	StringBuilder textoEmail = new StringBuilder();
	textoEmail.append(razaoSocialFornecedor).append(NLNL).append(emailLembrete).append(NLNL);

	if (gerarHtml) {
	    textoEmail.append(montarItensFornecedorCorporEmailHTML(afsSelecionadas, isParcelasAtrasadas)).append(NLNL);
	} else {
	    textoEmail.append(montarItensFornecedorCorporEmail(afsSelecionadas)).append(NLNL);
	}

	textoEmail.append(emailVisualizar).append(NLNL).append(chefeServicoComprasFormatado);

	return textoEmail.toString();
    }

    private String montarEmailAFsAtrasadas(List<ParcelasAFPendEntVO> afsSelecionadas, Integer numeroAutorizacaoFornecedor,
	    boolean gerarHtml, boolean isParcelasAtrasadas) throws ApplicationBusinessException {
	// C1
	ScoFornecedor fornecedor = afsSelecionadas.get(0).getFornecedor();
	// getScoFornecedorDAO().obterFornecedorPorAF(numeroAutorizacaoFornecedor);
	String razaoSocialFornecedor = getResourceBundleValue("EMAIL_PARCELA_ATRASADA_FORNECEDOR", fornecedor.getRazaoSocial());

	if (!gerarHtml) {
	    razaoSocialFornecedor = razaoSocialFornecedor.replaceAll(BR, "n");
	}

	// C2
	String razaoSocialHospital = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
	String emailUrgencia = getResourceBundleValue("EMAIL_PARCELA_ATRASADA_URGENCIA", razaoSocialHospital);
	if (!gerarHtml) {
	    emailUrgencia = emailUrgencia.replaceAll(BR, "n");
	}
	razaoSocialHospital = emailUrgencia;

	String urlHospital = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_SITE);
	String emailVisualizar = getResourceBundleValue("EMAIL_PARCELA_ATRASADA_VISUALIZAR", urlHospital.toLowerCase());
	if (!gerarHtml) {
	    emailVisualizar = emailVisualizar.replaceAll(BR, "n");
	}

	RapPessoasFisicas chefeServicoCompras = getRegistroColaboradorFacade().obterChefeServicoComprasPorAghParametros();
	String chefeServicoComprasFormatado = getResourceBundleValue("EMAIL_PARCELA_ATRASADA_CHEFE_COMPRAS", chefeServicoCompras.getNome());
	if (!gerarHtml) {
	    chefeServicoComprasFormatado = chefeServicoComprasFormatado.replaceAll(BR, "n");
	}

	StringBuilder textoEmail = new StringBuilder();
	textoEmail.append(razaoSocialFornecedor).append(NLNL).append(razaoSocialHospital);

	if (gerarHtml) {
	    textoEmail.append(montarItensFornecedorCorporEmailHTML(afsSelecionadas, isParcelasAtrasadas)).append(NLNL);
	} else {
	    textoEmail.append(montarItensFornecedorCorporEmail(afsSelecionadas)).append(NLNL);
	}

	textoEmail.append(emailVisualizar).append(NLNL);

	if (!gerarHtml) {
	    textoEmail.append(chefeServicoComprasFormatado.replaceAll(BR, "n"));
	} else {
	    textoEmail.append(chefeServicoComprasFormatado);
	}

	return textoEmail.toString();
    }

    @SuppressWarnings("PMD.ConsecutiveLiteralAppends")
    private String montarItensFornecedorCorporEmail(List<ParcelasAFPendEntVO> afsSelecionadas) throws ApplicationBusinessException {
	int espacamento = 15;
	StringBuilder emailItensFornecedor = new StringBuilder(330);
	emailItensFornecedor.append("Nro AF").append(StringUtils.leftPad("AFP", espacamento))
		.append(StringUtils.leftPad("Item", espacamento)).append(StringUtils.leftPad("Parc", espacamento))
		.append(StringUtils.leftPad("Prev Entrega", espacamento)).append(StringUtils.leftPad("Dias Atraso", espacamento))
		.append(StringUtils.leftPad("Qtde", espacamento)).append(StringUtils.leftPad("Qtde Entregue", espacamento))
		.append(StringUtils.leftPad("Saldo Entrega", espacamento)).append(StringUtils.leftPad("Unid", espacamento))
		.append(StringUtils.leftPad("Código", espacamento)).append(StringUtils.leftPad("Material", espacamento))
		.append(StringUtils.leftPad("Gestor", espacamento)).append(StringUtils.leftPad("Telefone", espacamento)).append('n')
		.append("--------------------------------------------------------------------------------------------------------------")
		.append("--------------------------------------------------------------------------------------------------------------")
		.append("--------------------------------------------------------------------------------------------------").append('n');

	List<AutorizacaoFornecimentoItemFornecedorVO> listaMateriaisEmail = getAutorizacaoFornecimentoEmailAtrasoRN()
		.listarAFsMateriaisParaEmailAtraso(afsSelecionadas);

	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	for (AutorizacaoFornecimentoItemFornecedorVO autorizacaoFornecimentoItemFornecedorVO : listaMateriaisEmail) {
	    StringBuilder nroAf = new StringBuilder();
	    nroAf.append(autorizacaoFornecimentoItemFornecedorVO.getPropostaFornecedorLicitacaoNumero()).append('/')
		    .append(autorizacaoFornecimentoItemFornecedorVO.getNumeroComplemento());
	    emailItensFornecedor.append(nroAf.toString())
		    .append(StringUtils.leftPad(autorizacaoFornecimentoItemFornecedorVO.getAfp().toString(), espacamento))
		    .append(StringUtils.leftPad(autorizacaoFornecimentoItemFornecedorVO.getItem().toString(), espacamento))
		    .append(StringUtils.leftPad(autorizacaoFornecimentoItemFornecedorVO.getParc().toString(), espacamento))
		    .append(StringUtils.leftPad(dateFormat.format(autorizacaoFornecimentoItemFornecedorVO.getPrevEntrega()), espacamento));

	    DateTime dataAtual = new DateTime();
	    DateTime dataPrevEntrega = new DateTime(autorizacaoFornecimentoItemFornecedorVO.getPrevEntrega());

	    autorizacaoFornecimentoItemFornecedorVO.setDiasAtraso(Days.daysBetween(dataPrevEntrega, dataAtual).getDays());
	    emailItensFornecedor
		    .append(StringUtils.leftPad(autorizacaoFornecimentoItemFornecedorVO.getDiasAtraso().toString(), espacamento))

		    .append(StringUtils.leftPad(autorizacaoFornecimentoItemFornecedorVO.getQtd().toString(), espacamento))
		    .append(StringUtils.leftPad(autorizacaoFornecimentoItemFornecedorVO.getQtdEntregue().toString(), espacamento));

	    autorizacaoFornecimentoItemFornecedorVO.setSaldoEntrega(autorizacaoFornecimentoItemFornecedorVO.getQtd()
		    - autorizacaoFornecimentoItemFornecedorVO.getQtdEntregue());

	    emailItensFornecedor
		    .append(StringUtils.leftPad(autorizacaoFornecimentoItemFornecedorVO.getSaldoEntrega().toString(), espacamento))
		    .append(StringUtils.leftPad(autorizacaoFornecimentoItemFornecedorVO.getUnid(), espacamento))
		    .append(StringUtils.leftPad(autorizacaoFornecimentoItemFornecedorVO.getCodigoMaterial().toString(), espacamento))
		    .append(StringUtils.leftPad(autorizacaoFornecimentoItemFornecedorVO.getMaterial(), espacamento))
		    .append(definirNomeTelefoneGestor(autorizacaoFornecimentoItemFornecedorVO, false)).append('n');
	}

	return emailItensFornecedor.toString();
    }

    private String montarItensFornecedorCorporEmailHTML(List<ParcelasAFPendEntVO> afsSelecionadas, Boolean parcelasAtrasadas)
	    throws ApplicationBusinessException {
	StringBuilder emailItensFornecedor = new StringBuilder(350);
	emailItensFornecedor.append("<table><tr><th>Nro AF</th><th>AFP</th><th>Item</th><th>Parc</th><th>Prev Entrega</th>");
	if (parcelasAtrasadas) {
	    emailItensFornecedor.append("<th>Dias Atraso</th>");
	}
	emailItensFornecedor
		.append("<th>Qtde</th><th>Qtde Entregue</th><th>Saldo Entrega</th><th>Unid</th><th>Código</th><th>Material</th><th>Gestor</th><th>Telefone</th></tr>");

	List<AutorizacaoFornecimentoItemFornecedorVO> listaMateriaisEmail = getAutorizacaoFornecimentoEmailAtrasoRN()
		.listarAFsMateriaisParaEmailAtraso(afsSelecionadas);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	for (AutorizacaoFornecimentoItemFornecedorVO autorizacaoFornecimentoItemFornecedorVO : listaMateriaisEmail) {
	    emailItensFornecedor.append("<tr>");
	    StringBuilder nroAf = new StringBuilder();
	    nroAf.append(autorizacaoFornecimentoItemFornecedorVO.getPropostaFornecedorLicitacaoNumero()).append('/')
		    .append(autorizacaoFornecimentoItemFornecedorVO.getNumeroComplemento());
	    emailItensFornecedor.append(TABLE_CELL_TAG).append(nroAf.toString()).append(END_TABLE_CELL_TAG).append(TABLE_CELL_TAG)
		    .append(autorizacaoFornecimentoItemFornecedorVO.getAfp()).append(END_TABLE_CELL_TAG).append(TABLE_CELL_TAG)
		    .append(autorizacaoFornecimentoItemFornecedorVO.getItem()).append(END_TABLE_CELL_TAG).append(TABLE_CELL_TAG)
		    .append(autorizacaoFornecimentoItemFornecedorVO.getParc()).append(END_TABLE_CELL_TAG).append(TABLE_CELL_TAG)
		    .append(dateFormat.format(autorizacaoFornecimentoItemFornecedorVO.getPrevEntrega())).append(END_TABLE_CELL_TAG);

	    DateTime dataAtual = new DateTime();
	    DateTime dataPrevEntrega = new DateTime(autorizacaoFornecimentoItemFornecedorVO.getPrevEntrega());

	    Integer diasAtraso = Days.daysBetween(dataPrevEntrega, dataAtual).getDays();
	    autorizacaoFornecimentoItemFornecedorVO.setDiasAtraso(diasAtraso < 0 ? 0 : diasAtraso);

	    autorizacaoFornecimentoItemFornecedorVO.setSaldoEntrega(autorizacaoFornecimentoItemFornecedorVO.getQtd()
		    - autorizacaoFornecimentoItemFornecedorVO.getQtdEntregue());

	    if (parcelasAtrasadas) {
		emailItensFornecedor.append(TABLE_CELL_TAG).append(autorizacaoFornecimentoItemFornecedorVO.getDiasAtraso())
			.append(END_TABLE_CELL_TAG);
	    }

	    emailItensFornecedor.append(TABLE_CELL_TAG).append(autorizacaoFornecimentoItemFornecedorVO.getQtd()).append(END_TABLE_CELL_TAG);
	    emailItensFornecedor.append(TABLE_CELL_TAG).append(autorizacaoFornecimentoItemFornecedorVO.getQtdEntregue())
		    .append(END_TABLE_CELL_TAG);
	    emailItensFornecedor.append(TABLE_CELL_TAG).append(autorizacaoFornecimentoItemFornecedorVO.getSaldoEntrega())
		    .append(END_TABLE_CELL_TAG);
	    emailItensFornecedor.append(TABLE_CELL_TAG).append(autorizacaoFornecimentoItemFornecedorVO.getUnid())
		    .append(END_TABLE_CELL_TAG);
	    emailItensFornecedor.append(TABLE_CELL_TAG).append(autorizacaoFornecimentoItemFornecedorVO.getCodigoMaterial())
		    .append(END_TABLE_CELL_TAG);
	    emailItensFornecedor.append(TABLE_CELL_TAG).append(autorizacaoFornecimentoItemFornecedorVO.getMaterial())
		    .append(END_TABLE_CELL_TAG);
	    emailItensFornecedor.append(definirNomeTelefoneGestor(autorizacaoFornecimentoItemFornecedorVO, false));
	    emailItensFornecedor.append("</tr>");
	}
	emailItensFornecedor.append("</table>");

	return emailItensFornecedor.toString();
    }

    private String montarItensFornecedorCorporEmailCSV(List<ParcelasAFPendEntVO> afsSelecionadas) throws ApplicationBusinessException {
	StringBuilder emailItensFornecedor = new StringBuilder(153);
	emailItensFornecedor.append("Nro AF;AFP;Item;Parc;Prev Entrega;Dias Atraso;Qtde;Qtde Entregue")
		.append(";Saldo Entrega;Unid;Código;Material;Gestor;Telefone;Estimativa de Entregan");

	List<AutorizacaoFornecimentoItemFornecedorVO> listaMateriaisEmail = getAutorizacaoFornecimentoEmailAtrasoRN()
		.listarAFsMateriaisParaEmailAtraso(afsSelecionadas);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	for (AutorizacaoFornecimentoItemFornecedorVO autorizacaoFornecimentoItemFornecedorVO : listaMateriaisEmail) {
	    StringBuilder nroAf = new StringBuilder();
	    nroAf.append(autorizacaoFornecimentoItemFornecedorVO.getPropostaFornecedorLicitacaoNumero())
	    .append('/')
	    .append(autorizacaoFornecimentoItemFornecedorVO.getNumeroComplemento());
	    emailItensFornecedor.append(nroAf.toString()).append(';')
	    .append(autorizacaoFornecimentoItemFornecedorVO.getAfp()).append(';')
	    .append(autorizacaoFornecimentoItemFornecedorVO.getItem()).append(';')
	    .append(autorizacaoFornecimentoItemFornecedorVO.getParc()).append(';')
	    .append(dateFormat.format(autorizacaoFornecimentoItemFornecedorVO.getPrevEntrega())).append(';');

	    DateTime dataAtual = new DateTime();
	    DateTime dataPrevEntrega = new DateTime(autorizacaoFornecimentoItemFornecedorVO.getPrevEntrega());
	    autorizacaoFornecimentoItemFornecedorVO.setDiasAtraso(Days.daysBetween(dataPrevEntrega, dataAtual).getDays());

	    autorizacaoFornecimentoItemFornecedorVO.setSaldoEntrega(autorizacaoFornecimentoItemFornecedorVO.getQtd()
		    - autorizacaoFornecimentoItemFornecedorVO.getQtdEntregue());

	    emailItensFornecedor.append(autorizacaoFornecimentoItemFornecedorVO.getDiasAtraso()).append(';');
	    emailItensFornecedor.append(autorizacaoFornecimentoItemFornecedorVO.getQtd()).append(';');
	    emailItensFornecedor.append(autorizacaoFornecimentoItemFornecedorVO.getQtdEntregue()).append(';');
	    emailItensFornecedor.append(autorizacaoFornecimentoItemFornecedorVO.getSaldoEntrega()).append(';');
	    emailItensFornecedor.append(autorizacaoFornecimentoItemFornecedorVO.getUnid()).append(';');
	    emailItensFornecedor.append(autorizacaoFornecimentoItemFornecedorVO.getCodigoMaterial()).append(';');
	    emailItensFornecedor.append(autorizacaoFornecimentoItemFornecedorVO.getMaterial()).append(';');
	    emailItensFornecedor.append(definirNomeTelefoneGestor(autorizacaoFornecimentoItemFornecedorVO, true));
	    emailItensFornecedor.append('n');
	}

	return emailItensFornecedor.toString();
    }

    private String definirNomeTelefoneGestor(AutorizacaoFornecimentoItemFornecedorVO autorizacaoFornecimentoItemFornecedorVO, boolean isCSV)
	    throws ApplicationBusinessException {
	Integer matriculaGestor = null;
	Short vinculoGestor = null;
	StringBuilder nomeTelefoneGestor = new StringBuilder();

	if (autorizacaoFornecimentoItemFornecedorVO.getServidorGestor().getId().getMatricula() != null
		&& autorizacaoFornecimentoItemFornecedorVO.getServidorGestor().getId().getVinCodigo() != null) {
	    matriculaGestor = autorizacaoFornecimentoItemFornecedorVO.getServidorGestor().getId().getMatricula();
	    vinculoGestor = autorizacaoFornecimentoItemFornecedorVO.getServidorGestor().getId().getVinCodigo();
	} else if (autorizacaoFornecimentoItemFornecedorVO.getServidor().getId().getMatricula() != null
		&& autorizacaoFornecimentoItemFornecedorVO.getServidor().getId().getVinCodigo() != null) {
	    matriculaGestor = autorizacaoFornecimentoItemFornecedorVO.getServidor().getId().getMatricula();
	    vinculoGestor = autorizacaoFornecimentoItemFornecedorVO.getServidor().getId().getVinCodigo();
	}

	RapServidoresId idGestor = new RapServidoresId();
	idGestor.setVinCodigo(vinculoGestor);
	idGestor.setMatricula(matriculaGestor);

	RapPessoasFisicas gestor = getRegistroColaboradorFacade().obterRapPessoasFisicasPorServidor(idGestor);

	String nomeGestor = null;
	if (gestor.getNomeUsual() != null) {
	    nomeGestor = gestor.getNomeUsual();
	} else {
	    nomeGestor = gestor.getNome();
	}

	if (isCSV) {
	    nomeTelefoneGestor.append(nomeGestor).append(';');
	} else {
	    nomeTelefoneGestor.append(TABLE_CELL_TAG).append(nomeGestor).append(END_TABLE_CELL_TAG);
	}

	BigDecimal dddFoneHospital = getParametroFacade().buscarValorNumerico(AghuParametrosEnum.P_DDD_FONE_HOSPITAL);

	String foneHospital = null;
	if (gestor.getRapServidores() != null && gestor.getRapServidores().getId().getMatricula() != 0) {
	    BigDecimal foneDepartamentoCompras = getParametroFacade().buscarValorNumerico(AghuParametrosEnum.P_PREFIXO_FONE_HOSPITAL);
	    String ramalGestor = gestor.getRapServidores().getRamalTelefonico() != null ? gestor.getRapServidores().getRamalTelefonico()
		    .getNumeroRamal().toString() : "";
	    foneHospital = dddFoneHospital.toString().concat(" ").concat(ramalGestor).concat(" ")
		    .concat(foneDepartamentoCompras.toString());

	    if (isCSV) {
		nomeTelefoneGestor.append(foneHospital).append(';');
	    } else {
		nomeTelefoneGestor.append(TABLE_CELL_TAG).append(foneHospital).append(END_TABLE_CELL_TAG);
	    }

	} else {
	    BigDecimal foneDepartamentoCompras = getParametroFacade().buscarValorNumerico(AghuParametrosEnum.P_FONE_DEPT_COMPRAS);
	    foneHospital = dddFoneHospital.toString().concat(" ").concat(foneDepartamentoCompras.toString());

	    if (isCSV) {
		nomeTelefoneGestor.append(foneHospital).append(';');
	    } else {
		nomeTelefoneGestor.append(TABLE_CELL_TAG).append(foneHospital).append(END_TABLE_CELL_TAG);
	    }

	}

	return nomeTelefoneGestor.toString();
    }

    private boolean isTodosFornecedoresSemEmail(List<ParcelasAFPendEntVO> afsSelecionadas) throws ApplicationBusinessException {
	List<ScoFornecedor> fornecedoresSemEmail = new ArrayList<ScoFornecedor>();

	for (ParcelasAFPendEntVO parcelasAFPendEntVO : afsSelecionadas) {
	    if (parcelasAFPendEntVO.getContato() != null && (parcelasAFPendEntVO.getContato().getEMail() == null || parcelasAFPendEntVO.getContato().getEMail().isEmpty())) {
		fornecedoresSemEmail.add(parcelasAFPendEntVO.getFornecedor());
	    }
	}
	notificarFornecedoresSemEmail(fornecedoresSemEmail);

	return fornecedoresSemEmail.size() == afsSelecionadas.size() ? true : false;
    }

    protected ScoPedidoMatExpedienteDAO getScoPedidoMatExpedienteDAO() {
	return scoPedidoMatExpedienteDAO;
    }

    protected void setScoPedidoMatExpedienteDAO(ScoPedidoMatExpedienteDAO scoPedidoMatExpedienteDAO) {
	this.scoPedidoMatExpedienteDAO = scoPedidoMatExpedienteDAO;
    }

    protected IParametroFacade getParametroFacade() {
	return parametroFacade;
    }

    protected void setParametroFacade(IParametroFacade parametroFacade) {
	this.parametroFacade = parametroFacade;
    }

    protected AutorizacaoFornecimentoEmailAtrasoRN getAutorizacaoFornecimentoEmailAtrasoRN() {
	return autorizacaoFornecimentoEmailAtrasoRN;
    }

    protected void setAutorizacaoFornecimentoEmailAtrasoRN(AutorizacaoFornecimentoEmailAtrasoRN autorizacaoFornecimentoEmailAtrasoRN) {
	this.autorizacaoFornecimentoEmailAtrasoRN = autorizacaoFornecimentoEmailAtrasoRN;
    }

    protected ScoContatoFornecedorDAO getScoContatoFornecedorDAO() {
	return scoContatoFornecedorDAO;
    }

    protected void setScoContatoFornecedorDAO(ScoContatoFornecedorDAO scoContatoFornecedorDAO) {
	this.scoContatoFornecedorDAO = scoContatoFornecedorDAO;
    }

    protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
	return registroColaboradorFacade;
    }

    protected void setRegistroColaboradorFacade(IRegistroColaboradorFacade registroColaboradorFacade) {
	this.registroColaboradorFacade = registroColaboradorFacade;
    }

    @Override
    protected Log getLogger() {
	return LOG;
    }

    protected IServidorLogadoFacade getServidorLogadoFacade() {
	return this.servidorLogadoFacade;
    }
}
