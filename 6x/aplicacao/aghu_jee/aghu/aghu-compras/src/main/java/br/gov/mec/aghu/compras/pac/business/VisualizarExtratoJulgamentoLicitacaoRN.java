package br.gov.mec.aghu.compras.pac.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoContatoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoServicoDAO;
import br.gov.mec.aghu.compras.pac.vo.VisualizarExtratoJulgamentoLicitacaoVO;
import br.gov.mec.aghu.dominio.DominioVisaoExtratoJulgamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.suprimentos.vo.RelUltimasComprasPACVO;
import br.gov.mec.aghu.suprimentos.vo.RelUltimasComprasPACVOPai;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.AnexoEmail;
import br.gov.mec.aghu.core.mail.EmailUtil;


@Stateless
public class VisualizarExtratoJulgamentoLicitacaoRN extends BaseBusiness {

	private static final long serialVersionUID = -5875255349708260627L;
	
	private static final Log LOG = LogFactory.getLog(VisualizarExtratoJulgamentoLicitacaoRN.class);
	
	@Inject
	private ScoLicitacaoDAO scoLicitacaoDAO;
	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;
	@Inject
	private ScoContatoFornecedorDAO scoContatoFornecedorDAO;
	@Inject
	private ScoMaterialDAO scoMaterialDAO;
	@Inject
	private ScoServicoDAO scoServicoDAO;

	@Inject
	private EmailUtil emailUtil;
	
	@EJB
	public IParametroFacade parametroFacade;
	
	protected enum VisualizarExtratoJulgamentoLicitacaoRNExceptionCode implements BusinessExceptionCode {
		EMAIL_EX_JULG_FORNECEDOR_SEM_EMAIL_CADASTRADO;
	}

	
	public ScoLicitacao buscarLicitacaoPorNumero(Integer numeroPac) {
		return getScoLicitacaoDAO().buscarLicitacaoPorNumero(numeroPac);
	}
	
	public List<VisualizarExtratoJulgamentoLicitacaoVO> buscarPropostasFornecedor(Integer numeroPac) {
		List<VisualizarExtratoJulgamentoLicitacaoVO> retorno = getScoItemPropostaFornecedorDAO().buscarPropostasFornecedor(numeroPac);
		return reavaliaListaRetorno(retorno, numeroPac);
	}

	private List<VisualizarExtratoJulgamentoLicitacaoVO> reavaliaListaRetorno(List<VisualizarExtratoJulgamentoLicitacaoVO> retorno, Integer numeroPac) {
		List<VisualizarExtratoJulgamentoLicitacaoVO> out = new ArrayList<VisualizarExtratoJulgamentoLicitacaoVO>();
		for (VisualizarExtratoJulgamentoLicitacaoVO in : retorno) {
			out.add(alteraVO(in, numeroPac));
		}
		return out;
	}

	private VisualizarExtratoJulgamentoLicitacaoVO alteraVO(VisualizarExtratoJulgamentoLicitacaoVO in, Integer numeroPac) {
		ScoMaterial material = buscarMaterial(numeroPac, in.getItemSolicitacaoNumero());
		ScoServico servico = buscaServico(numeroPac, in.getItemSolicitacaoNumero());
		in.setCodigoMaterialServico(material != null ? material.getCodigo() : servico.getCodigo());
		in.setNomeMaterialServico(material != null ? material.getNome() : servico.getNome());
		in.setUnidadeMedica(calculaUnidadeMedica(in));
		in.setEmail(buscaEmail(in.getPfrFrnNumero()));
		return in;
	}

	private String buscaEmail(Integer pfrFrnNumero) {
		if (getScoContatoFornecedorDAO().buscaEmail(pfrFrnNumero).size() > 0) {
			return getScoContatoFornecedorDAO().buscaEmail(pfrFrnNumero).get(0).getEMail(); 
		} else {
			return null;
		}
	}

	private String calculaUnidadeMedica(VisualizarExtratoJulgamentoLicitacaoVO in) {
		if(in.getFatorConversao() == 1){
			return in.getUmdCodigo();
		}else{
			return in.getUmdCodigo()+" c/ "+in.getFatorConversao();
		}
	}

	private ScoMaterial buscarMaterial(Integer numeroPac, Short item) {
		return getScoMaterialDAO().buscarMaterial(numeroPac, item);
	}
	
	private ScoServico buscaServico(Integer numeroPac, Short item){
		return getScoServicoDAO().buscarServico(numeroPac, item);
	}
	
	public List<VisualizarExtratoJulgamentoLicitacaoVO> verificaVisao(DominioVisaoExtratoJulgamento visao, 
			List<VisualizarExtratoJulgamentoLicitacaoVO> list, List<VisualizarExtratoJulgamentoLicitacaoVO> listExtratoAux, VisualizarExtratoJulgamentoLicitacaoVO item){
		//#5516  RN01 E RN02
		List<VisualizarExtratoJulgamentoLicitacaoVO> listaFornecedor = new ArrayList<VisualizarExtratoJulgamentoLicitacaoVO>();
		List<VisualizarExtratoJulgamentoLicitacaoVO> listaPDF = new ArrayList<VisualizarExtratoJulgamentoLicitacaoVO>();
		
		if(visao.equals(DominioVisaoExtratoJulgamento.TODOS)){
			listaPDF = list;
		}else{
			listaPDF = listExtratoAux;
		}
		for (VisualizarExtratoJulgamentoLicitacaoVO itemFornecedor  : listaPDF) {
			if(item.getCgc().equals(itemFornecedor.getCgc())){
				listaFornecedor.add(itemFornecedor);
			}
		}
		return listaFornecedor;
	}

	public void validarContatosFornecedores(List<VisualizarExtratoJulgamentoLicitacaoVO> vos) throws ApplicationBusinessException {
		if(vos != null && !vos.isEmpty()) {
			for(VisualizarExtratoJulgamentoLicitacaoVO vo : vos) {
				List<ScoContatoFornecedor> contatos = getScoContatoFornecedorDAO().buscaContatosFornecedor(vo.getPfrFrnNumero());
				if(contatos == null || contatos.isEmpty()) {
					throw new ApplicationBusinessException(VisualizarExtratoJulgamentoLicitacaoRNExceptionCode.EMAIL_EX_JULG_FORNECEDOR_SEM_EMAIL_CADASTRADO, vo.getRazaoSocial());
				}
			}
		}
	}
	
	public void enviarEmail(Integer pac, VisualizarExtratoJulgamentoLicitacaoVO vo, byte[] pdf, RapServidores usuarioLogado, String nomeArquivoPDF) throws ApplicationBusinessException {
		String dominioHospital = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_DOMINIO_EMAIL);
		String remetente = usuarioLogado.getUsuario() + dominioHospital;
		String assunto = montaAssunto(pac, vo);
		StringBuffer contatosFornecedor = new StringBuffer(500);
		List<String> destinatarios;
		List<ScoContatoFornecedor> contatos;
		AnexoEmail anexoEmail = null;
		if(nomeArquivoPDF == null){
			anexoEmail = new AnexoEmail("PAC_"+pac+"_Fornecedor_"+vo.getPfrFrnNumero()+".pdf", pdf, "application/pdf");
		} else {
			anexoEmail = new AnexoEmail(nomeArquivoPDF+".pdf", pdf, "application/pdf");
		}	
		contatos = getScoContatoFornecedorDAO().buscaContatosFornecedor(vo.getPfrFrnNumero());
		if(contatos != null && !contatos.isEmpty()) {
			contatosFornecedor.append("<BR><BR><TABLE>");
			contatosFornecedor.append("<TH align=\"left\">"+getResourceBundleValue("LABEL_CONTATO")+"</TH>");
			contatosFornecedor.append("<TH align=\"left\">"+getResourceBundleValue("LABEL_FONE")+"</TH>");
			contatosFornecedor.append("<TH align=\"left\">"+getResourceBundleValue("LABEL_EMAIL")+"</TH>");
			for(ScoContatoFornecedor contato : contatos) {
				contatosFornecedor.append("<TR><TD>"+contato.getNome()+"</TD>");
				contatosFornecedor.append("<TD>"+ (contato.getDdd() != null ? "(" + contato.getDdd() + ")" : "") + (contato.getFone() != null ? contato.getFone() : "")+"</TD>");
				contatosFornecedor.append("<TD>"+contato.getEMail()+"</TD></TR>");
			}
			contatosFornecedor.append("</TABLE>");
		}

		for(ScoContatoFornecedor contato : contatos) {
			destinatarios = new ArrayList<String>();
			destinatarios.add(contato.getEMail());			
			getEmailUtil().enviaEmailHtml(remetente, destinatarios, null, assunto, montarCorpoEmail(pac, vo) + contatosFornecedor.toString(), anexoEmail);
			//getEmailUtil().enviaEmail(remetente, destinatarios, null, assunto, montarCorpoEmail(pac, vo) + contatosFornecedor.toString(), anexoEmail);
		}
	}
	
	private String montaAssunto(Integer pac, VisualizarExtratoJulgamentoLicitacaoVO vo) throws ApplicationBusinessException {
		StringBuffer textoHTML = new StringBuffer(500);
		
		String siglaHospital = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_PARAMETRO_HU);

		textoHTML.append(MessageFormat.format(getResourceBundleValue("EMAIL_EX_JULG_ASSUNTO"), siglaHospital, pac));
		
		return textoHTML.toString();
	}
	
	private String montarCorpoEmail(Integer pac, VisualizarExtratoJulgamentoLicitacaoVO vo) throws ApplicationBusinessException {

		StringBuffer textoHTML = new StringBuffer(500);
		
		String hospital = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		String siglaHospital = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_PARAMETRO_HU);

		textoHTML.append(MessageFormat.format(getResourceBundleValue("EMAIL_EX_JULG_SR_FORNECEDOR"), vo.getRazaoSocial()))
		.append(MessageFormat.format(getResourceBundleValue("EMAIL_EX_JULG_MSG_PT1"), hospital, siglaHospital, pac))
		.append(MessageFormat.format(getResourceBundleValue("EMAIL_EX_JULG_MSG_PT2"), siglaHospital));

		return textoHTML.toString();
	}
	
	public String geraArquivoCSV(List<RelUltimasComprasPACVO> dados, Integer numeroPac) throws IOException {
		File file = File.createTempFile("rel_ultimas_compras_mat_pac", ".csv");
		Writer out = new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1");

		out.write(this.gerarCabecalhoCSV());

		for (RelUltimasComprasPACVO linha : dados) {
			out.write(System.getProperty("line.separator"));
			out.write(gerarLinhaCSV(linha, numeroPac));
		}
		out.flush();
		out.close();

		return file.getAbsolutePath();
	}

	private String gerarLinhaCSV(RelUltimasComprasPACVO linha, Integer numeroPac) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		NumberFormat numberFormat = new DecimalFormat("#,##0.0000");
		StringBuilder texto = new StringBuilder();
		addText(linha.getNroItem().toString(), texto);
		addText(numeroPac.toString(), texto);
		addText(linha.getCodMaterial().toString(), texto);
		
		if (linha.getDescMaterial() != null){
		    addText(linha.getDescMaterial().toString().replaceAll(",", ""), texto);
		} else {
			addText(";", texto);
	    }		
	
		addText(linha.getDescUnidade(), texto);
		addText(linha.getNroSolicitacao().toString(), texto);
		addText(linha.getNroLicit().toString(), texto);
		addText(this.obtemCodigoModalidade(linha.getTpModLicitacao()), texto);
		addText(linha.getInciso(), texto);
		if(linha.getDtAberturaProposta() != null){
			addText(sdf.format(new Date(linha.getDtAberturaProposta().getTime())), texto);
		} else {
			addText(";", texto);
	    }	
		
		addText(linha.getNumeroAF(), texto);
		addText(linha.getNrsSeq().toString(), texto);
		addText(sdf.format(new Date(linha.getDtGeracao().getTime())), texto);
		addText(linha.getNroNf().toString(), texto);
		addText(linha.getFormaPag(), texto);
		addText(linha.getQuantidade().toString(), texto);
		addText(linha.getDescFornecedor(), texto);
		
		if (linha.getFoneFornecedor() != null){
		    addText(linha.getFoneFornecedor().toString(), texto);
		}else {
			addText(";", texto);
		}
		
		addText(linha.getDescMarca(), texto);		
		
		Double vlrUnit = Double.valueOf(linha.getValor());
		
		if (vlrUnit != null){			
		    addText(numberFormat.format(vlrUnit).toString().replaceAll(",", "."), texto);
		} else {
			addText(";", texto);
	    }
		
		return texto.toString();
	}

	private String gerarCabecalhoCSV() {
		StringBuffer cabecalho = new StringBuffer() 
			.append("Item")
			.append(';')
			.append("PAC informado")
			.append(';')
			.append(this.getResourceBundleValue("CSV_CODIGO_MATERIAL"))
			.append(';')
			.append(this.getResourceBundleValue("CSV_DESCRICAO_MATERIAL"))
			.append(';')
			.append("Unid")
			.append(';')
			.append(this.getResourceBundleValue("CSV_SOLICITACAO"))
			.append(';')
			.append("PAC")
			.append(';')
			.append("Modalidade")
			.append(';')
			.append("Inciso")
			.append(';')
			.append("Data Abertura")
			.append(';')
			.append("AF")
			.append(';')
			.append("NR")
			.append(';')
			.append("Data NR")
			.append(';')
			.append("NF")
			.append(';')
			.append("Pgto")
			.append(';')
			.append("Qtde")
			.append(';')
			.append("Fornecedor")
			.append(';')
			.append("Fone")
			.append(';')
			.append("Marca")
			.append(';')
			.append("Vlr Unit");
		
		return cabecalho.toString();
	}
	
	private void addText(Object texto, StringBuilder sb) {
		if (texto != null) {
			sb.append(texto);
		}
		sb.append(';');
	}

	@Deprecated
	public void downloaded(String fileName) throws IOException {	
//TODO Utilizar método da  download() da controller
//		FacesContext fc = FacesContext.getCurrentInstance();
//		HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
//
//		response.setContentType("text/csv");
//		response.setHeader("Content-Disposition", "attachment;filename="+ ARQUIVO_CSV_NOME+EXTENSAO_CSV_NOME);
//		response.getCharacterEncoding();
//		OutputStream out = response.getOutputStream();
//		Scanner scanner = new Scanner(new FileInputStream(fileName), "ISO_8859_1");
//		while (scanner.hasNextLine()) {
//			out.write(scanner.nextLine().getBytes("ISO_8859_1"));
//			out.write(System.getProperty("line.separator").getBytes("ISO_8859_1"));
//		}
//		scanner.close();
//		out.flush();
//		out.close();
//		fc.responseComplete();
	}
	
	private String obtemCodigoModalidade(String tipo) {
		if ("CV".equals(tipo)) {				// 01 – Convite
			return "Convite";
		} else if ("TP".equals(tipo)) {		// 02 - Tomada de Preços
			return "Tomada de Preços";
		} else if ("CC".equals(tipo)) {		// 03 - Concorrência
			return "Concorrência";
		} else if ("".equals(tipo)) {		// 04 - Concorrência Internacional --->exatamente como está no POJO ScoModalidadeLicitacao
			return "Concorrência Internacional";
		} else if ("PG".equals(tipo)) {		// 05 – Pregão
			return "Pregão";
		} else if ("DI".equals(tipo)) {		// 06 – Dispensa de Licitação
			return "Dispensa de Licitação";
		} else if ("IN".equals(tipo)) {		// 07 – Inexigibilidade
			return "Inexigibilidade";
		} else if ("CN".equals(tipo)) {		// 20 – Concurso
			return "Concurso";
		}
		return "";
	}

	public String geraArquivoCSVItem(List<RelUltimasComprasPACVOPai> dados,
			Integer numeroPAC) throws IOException {
		File file = File.createTempFile("rel_ultimas_compras_mat_pac", ".csv");
		Writer out = new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1");

		out.write(this.gerarCabecalhoCSV());

		for (RelUltimasComprasPACVOPai linha : dados) {
			for(RelUltimasComprasPACVO item : linha.getCompras()){
				out.write(System.getProperty("line.separator"));
				out.write(gerarLinhaCSV(item, numeroPAC));
			}	
		}
		out.flush();
		out.close();

		return file.getAbsolutePath();
	}
	
		
	protected ScoLicitacaoDAO getScoLicitacaoDAO() {
		return scoLicitacaoDAO;
	}

	protected void setScoLicitacaoDAO(ScoLicitacaoDAO scoLicitacaoDAO) {
		this.scoLicitacaoDAO = scoLicitacaoDAO;
	}
	
	

	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}

	protected void setScoItemPropostaFornecedorDAO(ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO) {
		this.scoItemPropostaFornecedorDAO = scoItemPropostaFornecedorDAO;
	}

	
	protected ScoContatoFornecedorDAO getScoContatoFornecedorDAO() {
		return scoContatoFornecedorDAO;
	}

	protected void setScoContatoFornecedorDAO(ScoContatoFornecedorDAO scoContatoFornecedorDAO) {
		this.scoContatoFornecedorDAO = scoContatoFornecedorDAO;
	}
	
	

	protected ScoMaterialDAO getScoMaterialDAO() {
		return scoMaterialDAO;
	}

	protected void setScoMaterialDAO(ScoMaterialDAO scoMaterialDAO) {
		this.scoMaterialDAO = scoMaterialDAO;
	}
	
	

	protected ScoServicoDAO getScoServicoDAO() {
		return scoServicoDAO;
	}

	protected void setScoServicoDAO(ScoServicoDAO scoServicoDAO) {
		this.scoServicoDAO = scoServicoDAO;
	}
	
	

	protected EmailUtil getEmailUtil() {
		return emailUtil;
	}

	protected void setEmailUtil(EmailUtil emailUtil) {
		this.emailUtil = emailUtil;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
