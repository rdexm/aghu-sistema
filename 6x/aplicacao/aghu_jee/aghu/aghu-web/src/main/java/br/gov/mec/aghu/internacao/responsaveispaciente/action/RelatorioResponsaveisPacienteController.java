package br.gov.mec.aghu.internacao.responsaveispaciente.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ResponsaveisPacienteVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

/**
 * @author ehgsilva
 */


public class RelatorioResponsaveisPacienteController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4841372590829816912L;

	@EJB
	private IInternacaoFacade internacaoFacade;
	
	private Integer seqInternacao;
	
	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<Object> colecao = new ArrayList<Object>(0);
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private static final Log LOG = LogFactory.getLog(RelatorioResponsaveisPacienteController.class);
	
	private final String PAG_CADASTRO_INTERNACAO = "internacao-cadastroInternacao";
	
	/**
	 * Método invocado na criação do componente.
	 */
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	
	
	public void print(Integer seqInternacao) throws ApplicationBusinessException, JRException, SystemException, IOException {
		try {
			this.seqInternacao = seqInternacao;
			List<ResponsaveisPacienteVO> listaResponsaveisPacienteVO = internacaoFacade.obterRelatorioResponsaveisPaciente(getSeqInternacao());
			colecao = new ArrayList<Object>();
			colecao.addAll(listaResponsaveisPacienteVO);

			// #28740 - Necessidade de impressão de 2 cópias.
			for (int i = 0; i < 2; i++) {
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			}

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (OptimisticLockException e) {
			LOG.error("Erro gerar o relatório", e);
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error("Erro gerar o relatório", e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}


	@Override
	public Collection<Object> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/internacao/report/relatorioResponsaveisPaciente.jasper";
	}
	

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();  

		ResponsaveisPacienteVO responsavelPacienteVO = (ResponsaveisPacienteVO) this.colecao
				.get(0);
		String texto = responsavelPacienteVO.getDescContrato();
		params.put("descricaoContrato", texto);
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo2());
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}
		
        return params;
	}

	
	public String voltar(){
		return PAG_CADASTRO_INTERNACAO;
	}
	public Integer getSeqInternacao() {
		return seqInternacao;
	}

	public void setSeqInternacao(Integer seqInternacao) {
		this.seqInternacao = seqInternacao;
	}		

	public List<Object> getColecao() {
		return colecao;
	}

	public void setColecao(List<Object> colecao) {
		this.colecao = colecao;
	}
	
}
