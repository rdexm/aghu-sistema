package br.gov.mec.aghu.compras.pac.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.vo.AtaRegistroPrecoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.report.DocumentoJasper;


public class AtaRegistroPrecoController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(AtaRegistroPrecoController.class);

	private static final long serialVersionUID = -6065035007490402765L;
	
	private DocumentoJasper documento;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	private Integer nroPac;
	private Integer fornecedor;
	private AtaRegistroPrecoVO ataRegistroPrecoVO;
	private List<AghParametrosVO> listaParamentros;
	private Boolean existeDados;

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/ataRegistroPreco.jasper";
	}
	
	public DocumentoJasper gerarDoc() throws ApplicationBusinessException{
		return this.gerarDocumento();
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
        Map<String, Object> params = null;
		try {
            String[] nomeParametros = new String[]{"P_HOSPITAL_RAZAO_SOCIAL",
                    "P_HOSPITAL_END_LOGRADOURO",
                    "P_HOSPITAL_CGC",
                    "P_HOSPITAL_SIGLA",
                    "P_HOSPITAL_PRESIDENTE",
                    "P_HOSPITAL_END_CIDADE",
                    "P_HOSPITAL_ESTADO",
                    "P_AGHU_RELATORIOS_CABECALHO",
                    "P_AGHU_HOSPITAL_UF_SIGLA",
                    "P_HOSPITAL_END_CEP",
                    "P_HOSPITAL_END_FONE",
                    "P_AGHU_HOSPITAL_FAX",
                    "P_AGHU_HOSPITAL_SITE",
                    "P_AGHU_MODALIDADES_ARP"};

            List<AghParametros> listaParamentros = this.parametroFacade.obterPorVariosNomes(nomeParametros);

            List<ScoLicitacao> licitacoes = executarConsultasParametros(listaParamentros);

            preencherConteudoRelatorio(licitacoes, listaParamentros);

            if (this.ataRegistroPrecoVO == null) {
                this.ataRegistroPrecoVO = new AtaRegistroPrecoVO();
            }

            params = bindParametros();

            params.put("docLicitacaocAnoAceite", getDocLicitacaocAnoAceite());

            params.put("texto", this.getText());
        }catch (ApplicationBusinessException ex){
            apresentarExcecaoNegocio(ex);
        }
		
		return params;
	}

	protected Map<String, Object> bindParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		
		String cgc = validarString(this.ataRegistroPrecoVO.getCgc());
		
		params.put("licitacao", this.ataRegistroPrecoVO.getLicitacao() != null ? this.ataRegistroPrecoVO.getLicitacao().toString() : ""); 
		params.put("docLicitacao", this.ataRegistroPrecoVO.getDocLicitacao() != null ? this.ataRegistroPrecoVO.getDocLicitacao().toString() : "");
		params.put("anoAceite", validarString(this.ataRegistroPrecoVO.getDtLimiteAceitePropostaStr()));
		params.put("razaoSocialHospital", validarString(this.ataRegistroPrecoVO.getRazaoSocialHospital()));
		params.put("endereçoHospital", validarString(this.ataRegistroPrecoVO.getEnderecoHospital()));
		params.put("cnpjHospital", validarString(this.ataRegistroPrecoVO.getCnpj()));
		params.put("siglaHospital", validarString(this.ataRegistroPrecoVO.getSiglaHospital()));
		params.put("presidenteHospital", validarString(this.ataRegistroPrecoVO.getPresidenteHospital()));
		params.put("descricaoLicitacao", validarString(this.ataRegistroPrecoVO.getDescricaoLicitacao()));
		params.put("dataAbertura", validarString(this.ataRegistroPrecoVO.getDthrAberturaPropostaStr()));
		params.put("razaoSocialForn", validarString(this.ataRegistroPrecoVO.getRazaoSocialForn()));
		params.put("logradouroForn", validarString(this.ataRegistroPrecoVO.getLogradouroForn()));
		params.put("numero", validarString(this.ataRegistroPrecoVO.getNroLogradouro()));
		params.put("cidade", validarString(this.ataRegistroPrecoVO.getCidade()));
		params.put("estado", validarString(this.ataRegistroPrecoVO.getUfSigla()));
		params.put("cnpj", cgc+" "+validarString(this.ataRegistroPrecoVO.getCnpj()));
		params.put("cidadeHospital", validarString(this.ataRegistroPrecoVO.getCidadeHospital()));
		params.put("estadoHospital", validarString(this.ataRegistroPrecoVO.getEstadoHospital()));
		params.put("logotipoCabecalho", validarString(this.ataRegistroPrecoVO.getLogotipoCabecalho()));
		params.put("telefoneHospital", validarString(this.ataRegistroPrecoVO.getTelefoneHospital()));
		params.put("faxHospital", validarString(this.ataRegistroPrecoVO.getFaxHospital()));
		params.put("enderecoHospital", validarString(this.ataRegistroPrecoVO.getEnderecoHospital()));
		params.put("siglaEstadoHospital", validarString(this.ataRegistroPrecoVO.getUfSigla()));
		params.put("cepHospital", validarString(this.ataRegistroPrecoVO.getCepHospital()));
		params.put("siteHospital", validarString(this.ataRegistroPrecoVO.getSiteHospital()));
		
		return params;
	}
	private String getDocLicitacaocAnoAceite(){
		StringBuffer docLicitacaocAnoAceite = new StringBuffer(this.ataRegistroPrecoVO.getDocLicitacao() != null ? this.ataRegistroPrecoVO.getDocLicitacao().toString() : "")
		.append(" / ").append(validarString(this.ataRegistroPrecoVO.getDtLimiteAceitePropostaStr()));
		
		return docLicitacaocAnoAceite.toString();
	}

	private String getText(){
		StringBuffer text = new StringBuffer("<b>Realizador: ").append(validarString(this.ataRegistroPrecoVO.getRazaoSocialHospital())).append("</b>, com sede na ").append(validarString(this.ataRegistroPrecoVO.getEnderecoHospital())).append(", ")
		.append("inscrito no CNPJ sob o n° ").append(validarString(this.ataRegistroPrecoVO.getCnpj())).append(", doravante ")
		.append("designado ").append(validarString(this.ataRegistroPrecoVO.getSiglaHospital())).append(", representado legalmente por seu Presidente, <b>").append(validarString(this.ataRegistroPrecoVO.getPresidenteHospital())).append("</b>.<br/><br/>")
		.append("Objeto: <b>").append(validarString(this.ataRegistroPrecoVO.getDescricaoLicitacao()))
		.append("</b> conforme quantidades e especificações estabelecidas no termo de referência do <b>")
		.append("pregão eletrônico n° ").append( this.ataRegistroPrecoVO.getDocLicitacao() != null ? this.ataRegistroPrecoVO.getDocLicitacao().toString() : "").append("/").append(validarString(this.ataRegistroPrecoVO.getDtLimiteAceitePropostaStr())).append("</b>, que integra o presente instrumento, ")
		.append("independentemente de transcrição. <br/><br/>")
		.append("<b>Fornecimento conforme a demanda do ").append(validarString(this.ataRegistroPrecoVO.getSiglaHospital())).append(".<br/><br/>")
		.append("Data de sessão pública: ").append(validarString(this.ataRegistroPrecoVO.getDthrAberturaPropostaStr())).append("<br/><br/>")
		.append("Autora da proposta/lance classificado em primeiro lugar: ").append(validarString(this.ataRegistroPrecoVO.getRazaoSocialForn())).append(", ")
		.append("</b>com sede na ").append(validarString(this.ataRegistroPrecoVO.getLogradouroForn())).append(", n° ").append(this.ataRegistroPrecoVO.getNroLogradouro() != null ? this.ataRegistroPrecoVO.getNroLogradouro().toString() : "").append(", ")
		.append("cidade ").append(validarString(this.ataRegistroPrecoVO.getCidade())).append(", Estado ").append(validarString(this.ataRegistroPrecoVO.getUfSigla())).append(", inscrita no CNPJ sob o n° ").append(validarString(this.ataRegistroPrecoVO.getCnpj())).append(", ")
		.append("doravante designada <b>FORNECEDORA</b> por sua representante legal, que assina a ")
		.append("presente ata. <br/><br/>")
		.append("<b>Procedimento quando das contratações: <br/><br/>")
		.append("1. A existência de preços registrados não obriga o ").append(validarString(this.ataRegistroPrecoVO.getSiglaHospital())).append(" nem os ")
		.append("órgãos/entidades participantes a firmar as contratações que deles ")
		.append("poderão advir, facultando-se a realização de licitação específica ")
		.append("para o fornecimento pretendido</b>. <br/><br/>")
		.append("2. As contratações serão efetivadas e formalizadas mediante autorização(ões) de ")
		.append("fornecimento e emissão de nota de empenho, conforme necessidades do ").append(validarString(this.ataRegistroPrecoVO.getSiglaHospital())).append(".<br/>")
		.append("<b><br/>Forma e condições de pagamento (do ").append(validarString(this.ataRegistroPrecoVO.getSiglaHospital())).append(" )</b>: conforme condições previstas ")
		.append("em edital.<br/><br/>")
		.append("<b>Itens, descrição, quantidade, e preços unitérios</b>: conforme extrato ")
		.append("julgamento licitação.<br/><br/>")
		.append("<b>Gestão e Fiscalização</b>: Gestor e Fiscal da ARP conforme previsto no processo ")
		.append("administrativo. <br/><br/>")
		.append("<b>Obrigações</b>: Conforme previstas em edital.<br/><br/>")
		.append("<b>Sanções</b>: Conforme previstas em edital.<br/><br/>")
		.append("<b>Vigência</b>: 12 meses.<br/><br/>")
		.append("<b>Utilização da ARP por entidades não participantes</b>: <br/><br/>")
		.append("Não será admitida a utilização da ARP deste processo, por entidades não ")
		.append("participantes (carona).<br/><br/>")
		.append("<b>Foro</b>:<br/>")
		.append("Fica eleito o foro da Justiça Federal de Porto Alegre, RIO GRANDE DO SUL, para ")
		.append("dirimir eventual litígio decorrente desta ARP.<br/><br/>")
		.append("E, por estarem de acordo, firmam a presente ARP, em três vias de igual teor e forma. <br/><br/>")
		.append(validarString(this.ataRegistroPrecoVO.getCidadeHospital())).append(", ") ;
		
		return text.toString();
	}

	private List<ScoLicitacao> executarConsultasParametros(List<AghParametros> listaParamentros) {
		String paramVlrC2 = this.obterValorParametroModalidadesParametrizadas(listaParamentros);
				
		String modalidades[] = paramVlrC2.split(",");
		
		trimModalidades(modalidades);
                               // licitacao = this.pacFacade.buscarLicitacaoPorNumero(numeroPac);
		List<ScoLicitacao> licitacoes = this.comprasFacade.buscarLicitacaoPorNumero(this.nroPac, modalidades);
		return licitacoes;
	}

	protected void trimModalidades(String[] modalidades) {
		for(int i = 0; i < modalidades.length; i++){
			modalidades[i] = modalidades[i].trim();
		}
	}

	private void preencherConteudoRelatorio(List<ScoLicitacao> licitacoes, List<AghParametros> listaParamentros) throws ApplicationBusinessException{
		//if(licitacoes != null && licitacoes.size() > 0){
		this.ataRegistroPrecoVO = this.comprasFacade.pesquisarInfoAtaDeRegistroDePreco(nroPac, fornecedor);
		//}
		
		if(this.ataRegistroPrecoVO != null && this.ataRegistroPrecoVO.getLicitacao() != null){
			this.existeDados = Boolean.TRUE;
			if(listaParamentros != null && listaParamentros.size() > 0){
				for(AghParametros vo : listaParamentros){
					if("P_HOSPITAL_RAZAO_SOCIAL".equals(vo.getNome())){
						this.ataRegistroPrecoVO.setRazaoSocialHospital(vo.getVlrTexto());
					}
					else if("P_HOSPITAL_END_LOGRADOURO".equals(vo.getNome())){
						this.ataRegistroPrecoVO.setEnderecoHospital(vo.getVlrTexto());
					}
					else if("P_HOSPITAL_CGC".equals(vo.getNome())){
						this.ataRegistroPrecoVO.setCnpj(vo.getVlrTexto());
					}
					else if("P_HOSPITAL_SIGLA".equals(vo.getNome())){
						this.ataRegistroPrecoVO.setSiglaHospital(vo.getVlrTexto());
					}
					else if("P_HOSPITAL_PRESIDENTE".equals(vo.getNome())){
						this.ataRegistroPrecoVO.setPresidenteHospital(vo.getVlrTexto());
					}
					else if("P_HOSPITAL_END_CIDADE".equals(vo.getNome())){
						this.ataRegistroPrecoVO.setCidadeHospital(vo.getVlrTexto());
					}
					else if("P_HOSPITAL_ESTADO".equals(vo.getNome())){
						this.ataRegistroPrecoVO.setEstadoHospital(vo.getVlrTexto());
					}
					else if("P_AGHU_RELATORIOS_CABECALHO".equals(vo.getNome())){
                        if (vo.getVlrTexto() != null  && vo.getVlrTexto().contains("/")) {
                            String[] segments = vo.getVlrTexto().split("/");
                            String path_relativo = segments[segments.length - 1];
                            this.ataRegistroPrecoVO.setLogotipoCabecalho(path_relativo);
                        }else{
                            this.ataRegistroPrecoVO.setLogotipoCabecalho(vo.getVlrTexto());
                        }
					}
					else if("P_AGHU_HOSPITAL_UF_SIGLA".equals(vo.getNome())){
						this.ataRegistroPrecoVO.setUfSigla(vo.getVlrTexto());
					}
					else if("P_HOSPITAL_END_CEP".equals(vo.getNome())){
						this.ataRegistroPrecoVO.setCepHospital(vo.getVlrTexto());
					}
					else if("P_HOSPITAL_END_FONE".equals(vo.getNome())){
						this.ataRegistroPrecoVO.setTelefoneHospital(vo.getVlrTexto());
					}
					else if("P_AGHU_HOSPITAL_FAX".equals(vo.getNome())){
						this.ataRegistroPrecoVO.setFaxHospital(vo.getVlrTexto());
					}
					else if("P_AGHU_HOSPITAL_SITE".equals(vo.getNome())){
						this.ataRegistroPrecoVO.setSiteHospital(vo.getVlrTexto());
					}
				}
			}	
		} else {
			this.existeDados = Boolean.FALSE;
		}
	}
	
	private String obterValorParametroModalidadesParametrizadas(List<AghParametros> listaParamentros){
		if(listaParamentros != null && listaParamentros.size() > 0){
			for(AghParametros vo : listaParamentros){
				if("P_AGHU_MODALIDADES_ARP".equals(vo.getNome())){
					return vo.getVlrTexto();
				}
			}
		}
		return "";
	}

	//Getter e Setter
	
	public void setDocumento(DocumentoJasper documento) {
		this.documento = documento;
	}

	public DocumentoJasper getDocumento() {
		return documento;
	}

	public void setNroPac(Integer nroPac) {
		this.nroPac = nroPac;
	}

	public Integer getNroPac() {
		return nroPac;
	}

	public void setAtaRegistroPrecoVO(AtaRegistroPrecoVO ataRegistroPrecoVO) {
		this.ataRegistroPrecoVO = ataRegistroPrecoVO;
	}

	public AtaRegistroPrecoVO getAtaRegistroPrecoVO() {
		return ataRegistroPrecoVO;
	}

	public void setFornecedor(Integer fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Integer getFornecedor() {
		return fornecedor;
	}

	public void setListaParamentros(List<AghParametrosVO> listaParamentros) {
		this.listaParamentros = listaParamentros;
	}

	public List<AghParametrosVO> getListaParamentros() {
		return listaParamentros;
	}

	public void setExisteDados(Boolean existeDados) {
		this.existeDados = existeDados;
	}

	public Boolean getExisteDados() {
		return existeDados;
	}
	
	private String validarString(String value){
		if(value != null){
			return value;
		} else {
			return "";
		}
	}
	
	
	
	protected static Log getLog() {
		return LOG;
	}

	//METODO OBRIGATORIO PARA GERAR O RELATORIO MAS NAO É OBRIGATÓRIO DE SOBRESCREVER
	@Override
	public Collection<String> recuperarColecao() throws ApplicationBusinessException {
		
		List<String> lista = new ArrayList<String>();
		lista.add("1");
		return lista;
	}

}
