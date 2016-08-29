package br.gov.mec.aghu.compras.business;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.ScoPedidoMatExpedienteON.ScoPedidoMatExpedienteONExceptionCode;
import br.gov.mec.aghu.compras.dao.ScoPedItensMatExpedienteDAO;
import br.gov.mec.aghu.compras.dao.ScoPedidoMatExpedienteDAO;
import br.gov.mec.aghu.dominio.DominioValidacaoNF;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPedItensMatExpediente;
import br.gov.mec.aghu.model.ScoPedItensMatExpedienteId;
import br.gov.mec.aghu.model.ScoPedidoMatExpediente;
import br.gov.mec.aghu.papelaria.IPapelariaService;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sig.custos.vo.ItemPedidoPapelariaVO;
import br.gov.mec.aghu.sig.custos.vo.PedidoPapelariaVO;
import br.gov.mec.aghu.sig.custos.vo.PedidosPapelariaVO;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.ParametrosSistema;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ScoPedidoMatExpedienteRN extends BaseBMTBusiness {

	public enum ScoPedidoMatExpedienteRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_CONSULTA_CODIGO_AUTORIZACAO_PAPELARIA, ITEM_NAO_CONFERIDO_PEDIDO_MAT_EXPEDIENTE,MENSAGEM_ERRO_CONSULTA_EMAIL_FORNECEDOR,
		MENSAGEM_ERRO_CONSULTA_EMAIL_GESTOR_ALMOXARIFADO, MENSAGEM_ERRO_CONSULTA_EMAIL_SUPORTE, ERRO_CONSTRAINT_AGH_PMX_SER_FK1, ERRO_CONSTRAINT_AGH_PMX_SER_FK2,
		ERRO_CONSTRAINT_AGH_PMX_CCT_FK, ERRO_CONSTRAINT_AGH_PMX_SER_FK3, ERRO_SQL_ORA_01830, ERRO_SQL_ORA_02291, 
		ERRO_CONSTRAINT_AGH_IMX_MAT_FK, ERRO_CONSTRAINT_AGH_IMX_SER_FK1;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1732020161398027690L;
	
	private static final Log LOG = LogFactory.getLog(ScoPedidoMatExpedienteRN.class);
	
	@Inject
	private ScoPedidoMatExpedienteDAO scoPedidoMatExpedienteDAO;
	
	@Inject
	private ScoPedItensMatExpedienteDAO scoPedItensMatExpedienteDAO;
	
	@Inject
	private ParametrosSistema parametrosSistema;
	
	@Inject
	private IPapelariaService papelariaService;
	
	@EJB
	public IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	public IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	public ICentroCustoFacade centroCustoFacade;
	
	@Inject
	private EmailUtil emailUtil;

	@EJB
	public IParametroFacade parametroFacade;
	
	@Inject
	private MessagesUtils messagesUtils;	
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected void setPapelariaService(IPapelariaService papelariaService) {
		this.papelariaService = papelariaService;
	}


	public void atualizarScePedidoMatExpedienteById(Integer numeroNotaFiscal, DominioValidacaoNF situacao) throws ApplicationBusinessException {
		this.beginTransaction();
		List<ScoPedidoMatExpediente> listaPedidos = getScoPedidoMatExpedienteDAO().buscarPedidosPorNumeroNotaFiscal(numeroNotaFiscal);
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		for (ScoPedidoMatExpediente pedido : listaPedidos) {
			pedido.setServidorAlteracao(servidorLogado);
			pedido.setDataAlteracao(new Date());
			pedido.setIndValidacaoNotaFiscal(situacao);
			getScoPedidoMatExpedienteDAO().atualizar(pedido);
		}
		
		if (DominioValidacaoNF.R.equals(situacao)) {
			this.enviarEmail(numeroNotaFiscal);
		}
		this.commitTransaction();
		
	}


        public void atualizarScoPedidoMatExpediente(ScoPedidoMatExpediente pedido, Boolean recusa, RapServidores servidor)
    	    throws ApplicationBusinessException {
        	this.beginTransaction();
        	if (recusa) {
        	    pedido.setServidorConferente(servidor);
        	    pedido.setDataConferencia(new Date());
        	    pedido.setDataAlteracao(new Date());
        	    pedido.setServidorAlteracao(servidor);
        	    this.atualizar(pedido);
        	    
        	    List<ScoPedItensMatExpediente> itensDivergentesPedido = obterItensDivergentesPedido(pedido);
        	    enviarEmailValidacaoPedido(pedido, itensDivergentesPedido);
        	    
        	} else {
        	    validarConferenciaPedido(pedido);
        	    pedido.setIndValidacaoPedido(Boolean.TRUE);
        	    pedido.setServidorConferente(servidor);
        	    pedido.setDataConferencia(new Date());
        	    pedido.setDataAlteracao(new Date());
        	    pedido.setServidorAlteracao(servidor);
        	    this.atualizar(pedido);
        	}
        
        	if (!recusa) {
        	    /**
        	     * Chamada da procedure SCO_PEDIDOS_MAT_EXPEDIENTE
        	     */
        	    getScoPedidoMatExpedienteDAO().procedureGeraAfpAut(pedido.getNumeroPedido());
        	}
        	this.commitTransaction();
        
        }

	private void validarConferenciaPedido(ScoPedidoMatExpediente pedido)
	throws ApplicationBusinessException {
		List<ScoPedItensMatExpediente> itens = pedido.getListaItens();
		for (ScoPedItensMatExpediente scoPedItensMatExpediente : itens) {
			if (!scoPedItensMatExpediente.getIndValidacao()) {				
				throw new ApplicationBusinessException(
						ScoPedidoMatExpedienteRNExceptionCode.ITEM_NAO_CONFERIDO_PEDIDO_MAT_EXPEDIENTE);
			}
		}
	}

	private void enviarEmailValidacaoPedido(ScoPedidoMatExpediente pedido,
			List<ScoPedItensMatExpediente> itens) throws ApplicationBusinessException {

		AghParametros paramGestorAlmoxarifado = getParametroFacade()
		.buscarAghParametro(AghuParametrosEnum.P_EMAIL_COMPRAS_WEB);
		
		if (paramGestorAlmoxarifado == null) {
			throw new ApplicationBusinessException(
					ScoPedidoMatExpedienteRNExceptionCode.MENSAGEM_ERRO_CONSULTA_EMAIL_GESTOR_ALMOXARIFADO);
		}
		
		AghParametros paramFornecedor = getParametroFacade()
		.buscarAghParametro(AghuParametrosEnum.P_EMAIL_FORN_WEB);
		
		if (paramFornecedor == null) {
			throw new ApplicationBusinessException(
					ScoPedidoMatExpedienteRNExceptionCode.MENSAGEM_ERRO_CONSULTA_EMAIL_FORNECEDOR);
		}

		StringBuilder sbMensagem = new StringBuilder();

		String conteudo = messagesUtils.getResourceBundleValue("EMAIL_PEDIDO_MAT_EXPEDIENTE");
		String conteudoPedidos = this.gerarConteudoPedidos(pedido, itens);		
		String conteudoEmail = MessageFormat.format(conteudo, conteudoPedidos);

		String atenciosamente = messagesUtils.getResourceBundleValue("EMAIL_PEDIDO_MAT_EXPEDIENTE_ATENCIOSAMENTE");
		AghParametros usuarioAssinaEmail = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_USUARIO_RESP_WEB);
		atenciosamente = MessageFormat.format(atenciosamente, usuarioAssinaEmail.getVlrTexto());
		
		sbMensagem.append(conteudoEmail).append(atenciosamente);

		AghParametros remetenteParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO);
		String remetente = remetenteParametro.getVlrTexto();

		String assunto = messagesUtils.getResourceBundleValue("ASSUNTO_REJEICAO_PEDIDO_MAT_EXPEDIENTE") + " " + pedido.getNumeroPedido();

		List<String> destinatarios = new ArrayList<String>();

		StringTokenizer emailPara = new StringTokenizer(paramFornecedor.getVlrTexto(), ";");

		while (emailPara.hasMoreTokens()) {
			destinatarios.add(emailPara.nextToken().trim().toLowerCase());
		}
		
		StringTokenizer emailParaGestorAlmoxarifado = new StringTokenizer(paramGestorAlmoxarifado.getVlrTexto(), ";");

		while (emailParaGestorAlmoxarifado.hasMoreTokens()) {
			destinatarios.add(emailParaGestorAlmoxarifado.nextToken().trim().toLowerCase());
		}

		getEmailUtil().enviaEmail(remetente, destinatarios, null, assunto, sbMensagem.toString());
	}



	private String gerarConteudoNotas(Integer numeroNotaFiscal) {
		List<ScoPedidoMatExpediente> listaPedidos = getScoPedidoMatExpedienteDAO().pesquisarPedidosByNumeroNotaFiscal(numeroNotaFiscal);
		StringBuilder mensagemFormatada = new StringBuilder(85);

		for (ScoPedidoMatExpediente pedido : listaPedidos) {

			String dataNotaFiscal="";
			if(pedido.getDataNotaFiscal()!=null){
				dataNotaFiscal = DateUtil.obterDataFormatada(pedido.getDataNotaFiscal(), "dd/MM/yyyy");
			}
			
			mensagemFormatada.append(" <br> Nota Fiscal: ").append(pedido.getNumeroNotaFiscal())
			.append("&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Data Nota Fiscal: ").append(dataNotaFiscal)
			.append(" <br> ")
			.append(this.gerarConteudoPedidos(pedido, pedido.getListaItens()));

//			mensagemFormatada.append(" <br> ");
		}

		return mensagemFormatada.toString();
	}

	private void enviarEmail(Integer numeroNotaFiscal) throws ApplicationBusinessException {

		AghParametros paramFornecedor = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EMAIL_COMPRAS_WEB);

		if (paramFornecedor == null) {
			throw new ApplicationBusinessException(ScoPedidoMatExpedienteONExceptionCode.MENSAGEM_ERRO_CONSULTA_EMAIL_FORNECEDOR);
		}

//		AghParametros paramSuporte = getParametroFacade().buscarAghParametro(
//				AghuParametrosEnum.P_AGHU_EMAIL_SUPORTE_TECNICO);

//		if (paramSuporte == null) {
//			throw new AGHUNegocioException(
//					ScoPedidoMatExpedienteONExceptionCode.MENSAGEM_ERRO_CONSULTA_EMAIL_SUPORTE);
//		}

		StringBuilder sbMensagem = new StringBuilder();

		String conteudo = messagesUtils.getResourceBundleValue("CONTEUDO_EMAIL_REJEICAO_NOTA_FISCAL");

		String conteudoNotas = this.gerarConteudoNotas(numeroNotaFiscal);

		String conteudoEmail = MessageFormat.format(conteudo, conteudoNotas);

		sbMensagem.append(conteudoEmail);

		String remetente = getRemetente();

		List<String> destinatarios = new ArrayList<String>();
		//	destinatarios.add(paramSuporte.getVlrTexto());


		StringTokenizer emailPara = new StringTokenizer(paramFornecedor.getVlrTexto(), ";");

		while (emailPara.hasMoreTokens()) {
			destinatarios.add(emailPara.nextToken().trim().toLowerCase());
		}

		String assunto = messagesUtils.getResourceBundleValue("ASSUNTO_EMAIL_REJEICAO_NOTA_FISCAL") + numeroNotaFiscal;

		getEmailUtil().enviaEmail(remetente, destinatarios, null, assunto,	sbMensagem.toString());
	}

	private String getRemetente() throws ApplicationBusinessException {
		String remetente = parametrosSistema.getParametro("parametros_email");

		if (remetente == null) {
			throw new ApplicationBusinessException(
					ScoPedidoMatExpedienteONExceptionCode.MENSAGEM_PARAMETRO_EMAIL_NAO_CADASTRADO,
			"P_AGHU_EMAIL_ENVIO");
		}
		return remetente;
	}

	private String gerarConteudoPedidos(ScoPedidoMatExpediente pedido, List<ScoPedItensMatExpediente> itens) {

		StringBuilder mensagemFormatada = new StringBuilder(496);

		String dataPedido = DateUtil.obterDataFormatada(pedido.getDataPedido(), "dd/MM/yyyy");

		mensagemFormatada.append(" <br> Pedido: ").append(pedido.getNumeroPedido())
		.append(" &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp;  &nbsp;  Centro de Custo: ").append(pedido.getCentroCusto().getCodigo()) 
		.append("<br />")
		.append("Data Pedido: ").append(dataPedido)
		.append("&nbsp; &nbsp; &nbsp; Solicitante: ").append(pedido.getServidorSolicitante().getMatriculaVinculoNome())
		.append("<br> <br> <table> <tr> <td> Material  </td>     <td>&nbsp;  Descrição     </td>     <td>&nbsp;  Quantidade    </td>")
		.append("     <td>&nbsp;  Vlr. Unitário </td>     <td>&nbsp;  Vlr. Total    </td>   </tr> ");

		for (ScoPedItensMatExpediente item : itens) {
			mensagemFormatada.append(" <tr>   ")
			.append("<td> ").append( item.getMaterial().getCodigo()).append("</td>");
			
			if(StringUtils.isEmpty(item.getMaterial().getNome())) {
				mensagemFormatada.append("<td> &nbsp; </td>");
			} else {
				mensagemFormatada.append("<td> &nbsp; ").append(item.getMaterial().getNome()).append("</td>");
			}
			
			mensagemFormatada.append(" <td>  &nbsp;  &nbsp; " ).append(item.getValorUnitario()).append("</td> ")
			.append(" <td> &nbsp;  &nbsp; " ).append( this.getValorFormatado(item.getValorTotal())).append("</td> ")
			.append("  </tr>  ");
		}
		
		mensagemFormatada.append("  </table> <br>");

		return mensagemFormatada.toString();
	}

	private String getValorFormatado(BigDecimal valor) {
		Locale locBR = new Locale("pt", "BR");
		DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
		dfSymbols.setDecimalSeparator(',');
		DecimalFormat format = new DecimalFormat(
				"#,###,###,###,###,##0.0000####", dfSymbols);
		return (valor == null) ? null : format.format(valor);
	}

	public void atualizar(final ScoPedidoMatExpediente scoPedidoMatExpediente) {
		getScoPedidoMatExpedienteDAO().atualizar(scoPedidoMatExpediente);	
	}

	private List<ScoPedItensMatExpediente> obterItensDivergentesPedido(
			ScoPedidoMatExpediente pedido) {
//		List<ScoPedItensMatExpediente> itensDivergentesPedido = new ArrayList<ScoPedItensMatExpediente>();
//		for (ScoPedItensMatExpediente itemPedido : pedido.getListaItens()) {
//			if (!itemPedido.getIndValidacao()) {
//				itensDivergentesPedido.add(itemPedido);
//			}
//		}
//		return itensDivergentesPedido;
		return pedido.getListaItens();
	}


	/**
	 * Processo os pedidos de papelaria retornadados do XML(webservices) da Fabesul
	 * #35155
	 * 
	 * @param servidorLogado usuario logado no sistema
	 * @throws AGHUNegocioException
	 * @throws AGHUNegocioExceptionSemRollback
	 */
	public void processarPedidosPapelaria(RapServidores servidorLogado) throws ApplicationBusinessException {
		AghParametros paramCodigoAutorizacaoPapelaria = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PAPELARIA_CODIGO_AUTORIZACAO);

		AghParametros remetenteParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO);
		AghParametros destinatariosParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EMAIL_COMPRAS_WEB);

		if (paramCodigoAutorizacaoPapelaria == null) {
			throw new ApplicationBusinessException(
					ScoPedidoMatExpedienteRNExceptionCode.MENSAGEM_ERRO_CONSULTA_CODIGO_AUTORIZACAO_PAPELARIA);
		}

		AghParametros serviceAddress = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PAPELARIA_SERVICE_ADDRESS);
		PedidosPapelariaVO pedidosPapelaria = getPapelariaService().buscarPedidosPapelaria(paramCodigoAutorizacaoPapelaria.getVlrTexto(),serviceAddress.getVlrTexto());

		if(pedidosPapelaria != null && pedidosPapelaria.getPedidosPapelaria() != null  && pedidosPapelaria.getPedidosPapelaria().size() > 0) {
			
			for (PedidoPapelariaVO pedidoPapelaria : pedidosPapelaria.getPedidosPapelaria()) {
				try {
					this.beginTransaction(24*60*60*1000);
					ScoPedidoMatExpediente scoPedidoMatExpediente = getScoPedidoMatExpedienteDAO().obterScoPedidoMatExpedienteByNumeroPedido(pedidoPapelaria);
					if(scoPedidoMatExpediente != null) {
						if(permitirProcessamento(scoPedidoMatExpediente, pedidoPapelaria)) {
							if(scoPedidoMatExpediente.getNumeroNotaFiscal() == null && !scoPedidoMatExpediente.getIndValidacaoPedido()) {
								atualizarPedidoValida(scoPedidoMatExpediente, pedidoPapelaria);
								excluirItemPedidoValida(scoPedidoMatExpediente);
								List<ScoPedItensMatExpediente> itensPedido = gerarScoPedItensMatExpedientes(scoPedidoMatExpediente, servidorLogado, pedidoPapelaria);
								inserirItemPedido(itensPedido, scoPedidoMatExpediente, remetenteParametro, destinatariosParametro);
							} else if(pedidoPapelaria.getNumeroNotaFiscal() != null && scoPedidoMatExpediente.getIndValidacaoNotaFiscal() == DominioValidacaoNF.N) {
								atualizarPedido(scoPedidoMatExpediente, pedidoPapelaria);
							}						
						}
					} else {
						processarInsercaoPedido(servidorLogado, remetenteParametro, destinatariosParametro, pedidoPapelaria);
					}
				} catch(Exception e) {
					this.rollbackTransaction();
				} finally {
					this.commitTransaction();
				}
			}
		}
		
	}


	protected ScoPedItensMatExpedienteDAO getScoPedItensMatExpedienteDAO() {
		return scoPedItensMatExpedienteDAO;
	}


	protected void setScoPedItensMatExpedienteDAO(ScoPedItensMatExpedienteDAO scoPedItensMatExpedienteDAO) {
		this.scoPedItensMatExpedienteDAO = scoPedItensMatExpedienteDAO;
	}


	private void processarInsercaoPedido(RapServidores servidorLogado,
			AghParametros remetenteParametro,
			AghParametros destinatariosParametro,
			PedidoPapelariaVO pedidoPapelaria) throws ApplicationBusinessException {
		ScoPedidoMatExpediente scoPedidoMatExpediente = null;

		scoPedidoMatExpediente = geraScoPedidoMatExpediente(servidorLogado, pedidoPapelaria);
		
		//insert pedido
		try {
			scoPedidoMatExpediente.setSeq(null);
			getScoPedidoMatExpedienteDAO().persistir(scoPedidoMatExpediente);
			getScoPedidoMatExpedienteDAO().flush();
		} catch (PersistenceException e) {
			Map<String,BusinessExceptionCode> mapConstraintNameCode = new HashMap<String, BusinessExceptionCode>();
			mapConstraintNameCode.put("not-null property references a null or transient value: br.gov.mec.aghu.model.ScoPedidoMatExpediente.servidorSolicitante", 
					ScoPedidoMatExpedienteRNExceptionCode.ERRO_CONSTRAINT_AGH_PMX_SER_FK1);			
			mapConstraintNameCode.put("PMX_SER_FK1", ScoPedidoMatExpedienteRNExceptionCode.ERRO_CONSTRAINT_AGH_PMX_SER_FK1);
			mapConstraintNameCode.put("PMX_SER_FK2", ScoPedidoMatExpedienteRNExceptionCode.ERRO_CONSTRAINT_AGH_PMX_SER_FK2);
			mapConstraintNameCode.put("PMX_CCT_FK", ScoPedidoMatExpedienteRNExceptionCode.ERRO_CONSTRAINT_AGH_PMX_CCT_FK);
			mapConstraintNameCode.put("not-null property references a null or transient value: br.gov.mec.aghu.model.ScoPedidoMatExpediente.centroCusto", 
					ScoPedidoMatExpedienteRNExceptionCode.ERRO_CONSTRAINT_AGH_PMX_CCT_FK);			
			mapConstraintNameCode.put("PMX_SER_FK3", ScoPedidoMatExpedienteRNExceptionCode.ERRO_CONSTRAINT_AGH_PMX_SER_FK3);
			mapConstraintNameCode.put("not-null property references a null or transient value: br.gov.mec.aghu.model.ScoPedidoMatExpediente.servidorInclusao", 
					ScoPedidoMatExpedienteRNExceptionCode.ERRO_CONSTRAINT_AGH_PMX_SER_FK3);			
			mapConstraintNameCode.put("not-null property references a null or transient value: br.gov.mec.aghu.model.ScoPedidoMatExpediente.dataPedido", 
					ScoPedidoMatExpedienteRNExceptionCode.ERRO_SQL_ORA_01830);			
			mapConstraintNameCode.put("ORA-01830", ScoPedidoMatExpedienteRNExceptionCode.ERRO_SQL_ORA_01830);

			try {
				CoreUtil.validarConstraint(e, mapConstraintNameCode);
			} catch (ApplicationBusinessException e2) {
				enviarEmailNotificacaoPedido(scoPedidoMatExpediente.getNumeroPedido(), e2, remetenteParametro, destinatariosParametro);
			}

		}

		//insert item pedido
		List<ScoPedItensMatExpediente> itensPedido = gerarScoPedItensMatExpedientes(scoPedidoMatExpediente, servidorLogado, pedidoPapelaria);
		inserirItemPedido(itensPedido, scoPedidoMatExpediente, remetenteParametro, destinatariosParametro);
	}

	private void atualizarPedido(ScoPedidoMatExpediente scoPedidoMatExpediente, PedidoPapelariaVO pedidoPapelaria) throws ApplicationBusinessException {
		scoPedidoMatExpediente.setNumeroNotaFiscal(pedidoPapelaria.getNumeroNotaFiscal());
		scoPedidoMatExpediente.setDataNotaFiscal(pedidoPapelaria.getDataFaturamento());
		scoPedidoMatExpediente.setDataAlteracao(new Date());
		RapServidores usuarioGenerico = obterUsuarioGenerio();
		scoPedidoMatExpediente.setServidorAlteracao(usuarioGenerico);

		getScoPedidoMatExpedienteDAO().atualizar(scoPedidoMatExpediente);
		getScoPedidoMatExpedienteDAO().flush();
	}

	private void atualizarPedidoValida(ScoPedidoMatExpediente scoPedidoMatExpediente, PedidoPapelariaVO pedidoPapelaria) throws ApplicationBusinessException {
		scoPedidoMatExpediente.setNumeroPedido(pedidoPapelaria.getNumPedido());
		scoPedidoMatExpediente.setDataAlteracao(new Date());
		RapServidores usuarioGenerico = obterUsuarioGenerio();
		scoPedidoMatExpediente.setServidorAlteracao(usuarioGenerico);
		
		getScoPedidoMatExpedienteDAO().atualizar(scoPedidoMatExpediente);
		getScoPedidoMatExpedienteDAO().flush();
	}

	private void excluirItemPedidoValida(ScoPedidoMatExpediente scoPedidoMatExpediente) throws ApplicationBusinessException {
		List<ScoPedItensMatExpediente> itensDoPedido = this.getScoPedItensMatExpedienteDAO().pesquisarItensPedidoByNumeroPedido(scoPedidoMatExpediente.getNumeroPedido());

		if(itensDoPedido != null && itensDoPedido.size() > 0) {
			for (ScoPedItensMatExpediente scoPedItensMatExpediente : itensDoPedido) {
				this.getScoPedItensMatExpedienteDAO().remover(scoPedItensMatExpediente);
			}
		}
	}

	private void inserirItemPedido(List<ScoPedItensMatExpediente> itensPedido, ScoPedidoMatExpediente scoPedidoMatExpediente,
			AghParametros remetenteParametro,AghParametros destinatariosParametro) throws ApplicationBusinessException, PersistenceException {
		for (ScoPedItensMatExpediente scoPedItensMatExpediente : itensPedido) {
			try {
				this.getScoPedItensMatExpedienteDAO().persistir(scoPedItensMatExpediente);
			} catch (PersistenceException e) {
				Map<String,BusinessExceptionCode> mapConstraintNameCode = new HashMap<String, BusinessExceptionCode>();
				mapConstraintNameCode.put("IMX_PMX_FK", ScoPedidoMatExpedienteRNExceptionCode.ERRO_SQL_ORA_02291);
				mapConstraintNameCode.put("material", ScoPedidoMatExpedienteRNExceptionCode.ERRO_CONSTRAINT_AGH_IMX_MAT_FK);
				mapConstraintNameCode.put("mat_codigo", ScoPedidoMatExpedienteRNExceptionCode.ERRO_CONSTRAINT_AGH_IMX_MAT_FK);
				mapConstraintNameCode.put("IMX_MAT_FK", ScoPedidoMatExpedienteRNExceptionCode.ERRO_CONSTRAINT_AGH_IMX_MAT_FK);
				mapConstraintNameCode.put("IMX_SER_FK1", ScoPedidoMatExpedienteRNExceptionCode.ERRO_CONSTRAINT_AGH_IMX_SER_FK1);

				try {
					CoreUtil.validarConstraint(e, mapConstraintNameCode);
				} catch (ApplicationBusinessException e2) {
					enviarEmailNotificacaoPedido(scoPedidoMatExpediente.getNumeroPedido(), e2, remetenteParametro, destinatariosParametro);
				}
			}
		}
	}

	private boolean permitirProcessamento(ScoPedidoMatExpediente scoPedidoMatExpediente, PedidoPapelariaVO pedidoPapelaria) {
		if(pedidoPapelaria.getNumeroNotaFiscal() == null && scoPedidoMatExpediente.getIndValidacaoPedido()) {
			return false;
		}

		if(pedidoPapelaria.getNumeroNotaFiscal() != null && scoPedidoMatExpediente.getIndValidacaoNotaFiscal() == DominioValidacaoNF.S) {
			return false;
		}

		return true;
	}
	
	private RapServidores obterUsuarioGenerio() {
		RapServidores usuarioGenerico = null;

		try {
			AghParametros parametroVinculoUsuarioGenerico = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_VINCULO_RM_COMPRAS_WEB);
			AghParametros parametroMatriculaUsuarioGenerio = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MATRICULA_RM_COMPRAS_WEB);

			Integer matriculaUsuarioGenerico = parametroMatriculaUsuarioGenerio.getVlrNumerico().intValue();
			Short vinculoUsuarioGenerico = parametroVinculoUsuarioGenerico.getVlrNumerico().shortValue();

			usuarioGenerico = getRegistroColaboradorFacade().obterServidor(vinculoUsuarioGenerico, matriculaUsuarioGenerico);
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}

		return usuarioGenerico;
	}

	private void enviarEmailNotificacaoPedido(Integer numeroPedido, ApplicationBusinessException exceptionCode,
			AghParametros remetenteParametro, AghParametros destinatariosParametro) throws ApplicationBusinessException {
		String remetente = remetenteParametro.getVlrTexto();
		List<String> destinatariosOcultos = null;
		String assunto = messagesUtils.getResourceBundleValue("ASSUNTO_EMAIL_PROCESSAMENTO_PEDIDO_PAPELARIA");//"Compras WEB - HCPA";

		List<String> destinatarios = new ArrayList<String>();
		StringTokenizer emailPara = new StringTokenizer(destinatariosParametro.getVlrTexto(), ";");

		while (emailPara.hasMoreTokens()) {
			destinatarios.add(emailPara.nextToken().trim().toLowerCase());
		}
		//String mensagem = MessageFormat.format(SeamResourceBundle.getBundle().getString(exceptionCode.getCode().toString()), String.valueOf(numeroPedido)); 
		String mensagem = MessageFormat.format(exceptionCode.getMessage(), String.valueOf(numeroPedido));
		getEmailUtil().enviaEmail(remetente, destinatarios, destinatariosOcultos, assunto, mensagem);
	}


	private ScoPedidoMatExpediente geraScoPedidoMatExpediente(RapServidores servidorLogado, PedidoPapelariaVO pedidoPapelaria) {
		//cria pedido
		ScoPedidoMatExpediente scoPedidoMatExpediente = new ScoPedidoMatExpediente();
		scoPedidoMatExpediente.setNumeroPedido(pedidoPapelaria.getNumPedido());

		if(pedidoPapelaria.getDataPedido() != null) {
			try {
				Calendar dataPedido = Calendar.getInstance();
				dataPedido.setTime(pedidoPapelaria.getDataPedido());
				Calendar horaPedido = Calendar.getInstance();
				horaPedido.setTime(pedidoPapelaria.getHoraPedido());
				dataPedido.set(Calendar.HOUR_OF_DAY, horaPedido.get(Calendar.HOUR_OF_DAY));
				dataPedido.set(Calendar.MINUTE, horaPedido.get(Calendar.MINUTE));
				dataPedido.set(Calendar.SECOND, horaPedido.get(Calendar.SECOND));
				scoPedidoMatExpediente.setDataPedido(dataPedido.getTime());
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
		}

		String usuario = pedidoPapelaria.getUsuario();
		
		RapServidores servidorSolicitante = null;
		if(usuario != null) {
			try {
				Integer matricula = Integer.valueOf(usuario.substring(usuario.indexOf(Character.valueOf('-'))+1, usuario.length()));
				Short vinculo = Short.valueOf(usuario.substring(0, usuario.indexOf(Character.valueOf('-'))));
				servidorSolicitante = getRegistroColaboradorFacade().obterServidor(vinculo, matricula);
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
		}
		scoPedidoMatExpediente.setServidorSolicitante(servidorSolicitante);

		FccCentroCustos centroCusto = null;
		if(pedidoPapelaria.getCentroCusto() != null) {
			centroCusto = getCentroCustoFacade().obterCentroCustoPorChavePrimaria(pedidoPapelaria.getCentroCusto());
		}
		scoPedidoMatExpediente.setCentroCusto(centroCusto);

		RapServidores servidorInclusao = obterUsuarioGenerio();
		scoPedidoMatExpediente.setServidorInclusao(servidorInclusao);
		
		scoPedidoMatExpediente.setCentroCusto(centroCusto);
		scoPedidoMatExpediente.setDataConferencia(new Date());
		scoPedidoMatExpediente.setDataRecebimento(new Date());
		scoPedidoMatExpediente.setNumeroNotaFiscal(pedidoPapelaria.getNumeroNotaFiscal());
		scoPedidoMatExpediente.setDataCriacao(new Date());		
		scoPedidoMatExpediente.setIndGeracaoRM(false);
		scoPedidoMatExpediente.setIndIntegracaoEstoque(false);
		scoPedidoMatExpediente.setIndValidacaoPedido(false);
		scoPedidoMatExpediente.setIndValidacaoNotaFiscal(DominioValidacaoNF.N);
		return scoPedidoMatExpediente;
	}


	private List<ScoPedItensMatExpediente> gerarScoPedItensMatExpedientes(ScoPedidoMatExpediente scoPedidoMatExpediente, RapServidores servidorLogado,PedidoPapelariaVO pedidoPapelaria) {
		List<ScoPedItensMatExpediente> scoPedItensMatExpedientes = new ArrayList<ScoPedItensMatExpediente>();

		int itemNumero = 0;

		for (ItemPedidoPapelariaVO itemPedidoPapelariaVO : pedidoPapelaria.getItensPedidoPapelaria().getItensPedidoPapelaria()) {
			ScoPedItensMatExpediente scoPedItensMatExpediente = new ScoPedItensMatExpediente();
			ScoPedItensMatExpedienteId scoPedItensMatExpedienteId = new ScoPedItensMatExpedienteId();
			scoPedItensMatExpedienteId.setItemNumero(++itemNumero);
			scoPedItensMatExpedienteId.setPmxSeq(scoPedidoMatExpediente.getSeq());
			scoPedItensMatExpediente.setId(scoPedItensMatExpedienteId);
			scoPedItensMatExpediente.setQuantidade(itemPedidoPapelariaVO.getQuantidade());
			scoPedItensMatExpediente.setValorUnitario(itemPedidoPapelariaVO.getValorUnitario());
			ScoMaterial material = null;
			
			if(itemPedidoPapelariaVO.getCodigoProduto() != null) {
				material = new ScoMaterial();
				material.setCodigo(itemPedidoPapelariaVO.getCodigoProduto());
			}
			
			scoPedItensMatExpediente.setMaterial(material);
			scoPedItensMatExpediente.setServidorInclusao(obterUsuarioGenerio());
			scoPedItensMatExpediente.setDataCriacao(new Date());
			scoPedItensMatExpedientes.add(scoPedItensMatExpediente);
		}

		return scoPedItensMatExpedientes;
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


	protected IPapelariaService getPapelariaService() {
		return papelariaService;
	}


	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}


	protected void setRegistroColaboradorFacade(IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

	protected void setCentroCustoFacade(ICentroCustoFacade centroCustoFacade) {
		this.centroCustoFacade = centroCustoFacade;
	}

	protected EmailUtil getEmailUtil() {
		return emailUtil;
	}

	protected void setEmailUtil(EmailUtil emailUtil) {
		this.emailUtil = emailUtil;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	protected void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}
	
	


}