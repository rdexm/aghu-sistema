package br.gov.mec.aghu.blococirurgico.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProfissionaisUnidadeCirurgicaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioProfissionaisUnidadeCirurgicaController extends  ActionReport {
	
	private static final long serialVersionUID = -7322106307548311780L;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica[] itensOrdenacao = new DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica[] {
			DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica.NOME, DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica.FUNCAO, 
			DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica.CODIGO};
	
	/*
	 * CAMPOS DE FILTRO
	 */
	private AghUnidadesFuncionais unidadeCirurgica;
	private Boolean ativosInativos = true;
	private AghEspecialidades especialidade;
	private DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica ordenacao = DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica.NOME;
	
	private static final String RELATORIO_PROFISSIONAIS = "relatorioProfissionaisUnidadeCirurgica";
	private static final String RELATORIO_PROFISSIONAIS_PDF = "relatorioProfissionaisUnidadeCirurgicaPdf";

	private static final Log LOG = LogFactory.getLog(RelatorioProfissionaisUnidadeCirurgicaController.class);
	
		
	public String voltar() {
		return RELATORIO_PROFISSIONAIS;
	}
	
	/*
	 * MÉTODOS DAS SUGGESTIONS
	 */
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(this.blocoCirurgicoFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, false),listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return this.blocoCirurgicoFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, false);
	}
	
	public List<AghEspecialidades> pesquisarEspecialidadesProcedimentos(String objPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarPorNomeOuSiglaEspSeqNulo(objPesquisa != null ? objPesquisa : null),pesquisarEspecialidadesProcedimentosCount(objPesquisa));
	}

	public Long pesquisarEspecialidadesProcedimentosCount(String objPesquisa) {
		return this.aghuFacade.pesquisarPorNomeOuSiglaEspSeqNuloCount(objPesquisa != null ? objPesquisa : null);
	}
	
	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioProfissionaisUnidadeCirurgicaVO> colecao = new ArrayList<RelatorioProfissionaisUnidadeCirurgicaVO>();
		
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioProfissionaisUnidadeCirurgica.jasper";
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
		params.put("unidadeFuncional", this.unidadeCirurgica.getDescricao());
		
		String ordenacao = this.ordenacao.toString();
		if (StringUtils.equals(ordenacao, DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica.NOME.toString())) {
			ordenacao = "Nome do Profissional";
		} else if (StringUtils.equals(ordenacao, DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica.FUNCAO.toString())) {
			ordenacao = "Função do Profissional";
		} else if (StringUtils.equals(ordenacao, DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica.CODIGO.toString())) {
			ordenacao = "Vínculo e Matrícula do Profissional";
		}
		params.put("ordenacao", ordenacao);
		return params;
	}
	
	public String visualizarImpressao(){
		try {
			if (this.especialidade != null) {
				this.colecao = blocoCirurgicoFacade
					.listarProfissionaisPorUnidadeCirurgica(this.unidadeCirurgica.getSeq(), this.ativosInativos, this.especialidade.getSeq(), this.ordenacao);
			} else {
				this.colecao = blocoCirurgicoFacade
					.listarProfissionaisPorUnidadeCirurgica(this.unidadeCirurgica.getSeq(), this.ativosInativos, null, this.ordenacao);
			}
			if(colecao.isEmpty()) {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_PROFISSIONAIS_UNIDADE_CIRURGICA_VAZIO");
				return RELATORIO_PROFISSIONAIS;
			}
			return RELATORIO_PROFISSIONAIS_PDF;
			
		} catch (Exception e) {
			LOG.error("Excecao Capturada: ", e);
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ERRO_RELATORIO_PROFISSIONAIS_UNIDADE_CIRURGICA");
			return RELATORIO_PROFISSIONAIS;
		}
	}
	
	public void directPrint() {
		try {
			if (this.especialidade != null) {
				this.colecao = blocoCirurgicoFacade
					.listarProfissionaisPorUnidadeCirurgica(this.unidadeCirurgica.getSeq(), this.ativosInativos, this.especialidade.getSeq(), this.ordenacao);
			} else {
				this.colecao = blocoCirurgicoFacade
					.listarProfissionaisPorUnidadeCirurgica(this.unidadeCirurgica.getSeq(), this.ativosInativos, null, this.ordenacao);
			}
				
		} catch (Exception e) {
			LOG.error("Excecao Capturada: ", e);
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ERRO_RELATORIO_PROFISSIONAIS_UNIDADE_CIRURGICA");
			return;
		}
		
		if(colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_PROFISSIONAIS_UNIDADE_CIRURGICA_VAZIO");
			return;
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Relatório de Profissionais que Atuam em Unidade Cirúrgica");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		if (cascaFacade.usuarioTemPermissao(servidorLogadoFacade.obterServidorLogado().getUsuario(), "visualizarProfissionaisUnidadesCirurgicas", "imprimir")) {
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		} else {
			return this.criarStreamedContentPdf(documento.getPdfByteArray(true));
		}
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioProfissionaisUnidadeCirurgicaVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao; 
	}
	
	public void limparPesquisa(){
		this.unidadeCirurgica = null;
		this.ativosInativos = true;
		this.especialidade = null;
		this.ordenacao = DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica.NOME;
	}
	
	/*
	 * GETTERS AND SETTERS
	 */
	public AghUnidadesFuncionais getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public void setUnidadeCirurgica(AghUnidadesFuncionais unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}

	public Boolean getAtivosInativos() {
		return ativosInativos;
	}

	public void setAtivosInativos(Boolean ativosInativos) {
		this.ativosInativos = ativosInativos;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica[] getItensOrdenacao() {
		return itensOrdenacao;
	}

	public void setItensOrdenacao(
			DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica[] itensOrdenacao) {
		this.itensOrdenacao = itensOrdenacao;
	}

	public DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica getOrdenacao() {
		return ordenacao;
	}

	public void setOrdenacao(DominioOrdenacaoRelatorioProfissionaisUnidadeCirurgica ordenacao) {
		this.ordenacao = ordenacao;
	}

	public List<RelatorioProfissionaisUnidadeCirurgicaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioProfissionaisUnidadeCirurgicaVO> colecao) {
		this.colecao = colecao;
	}
}