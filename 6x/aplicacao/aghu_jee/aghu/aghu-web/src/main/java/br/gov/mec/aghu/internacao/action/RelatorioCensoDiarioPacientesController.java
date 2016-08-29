package br.gov.mec.aghu.internacao.action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;
import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.VAinCensoVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

import com.itextpdf.text.DocumentException;


public class RelatorioCensoDiarioPacientesController extends ActionReport {

        private static final long serialVersionUID = 4021910363640095385L;      

        private Short unidadeFuncionalSeq;
        private Date    data;
        private DominioSituacaoUnidadeFuncional  status;
        private Short unidadeFuncionalPai;
        private AghUnidadesFuncionais unidadeFuncional;
        private List<VAinCensoVO> voCensoDiario;
        private static final String VOLTAR_CENSO_DIARIO = "internacao-pesquisarCensoDiarioPacientes";

        @EJB
        private IPesquisaInternacaoFacade pesquisaInternacaoFacade; 

        @EJB
        private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
     
        @Inject
        private SistemaImpressao sistemaImpressao;
        
		
        
        @Override
        public String recuperarArquivoRelatorio() {
                return "/br/gov/mec/aghu/internacao/report/relatorioCensoDiarioPacientes.jasper"; 
        }     

	public Map<String, Object> recuperarParametros() {

    		Map<String, Object> params = new HashMap<String, Object>();	
		
    		try {
				recuperarListaCensoDiario();
			} catch (ApplicationBusinessException e) {
				this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}		  		

    		params.put("nomeHospital", cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoLocal());
    		Integer tamanhoLista = voCensoDiario.size();
    		params.put("tamanhoLista", tamanhoLista.toString());   				

    		if(unidadeFuncionalPai != null && unidadeFuncionalSeq == null){

    			params.put("nomeUnidadeFuncional", "");
    			params.put("nomeUnidadeFuncionalPai", unidadeFuncional.getDescricao());

    		} else {
    			params.put("nomeUnidadeFuncional", unidadeFuncional.getDescricao());
    			params.put("nomeUnidadeFuncionalPai", unidadeFuncional.getUnfSeq() == null ? "" : unidadeFuncional.getUnfSeq().getDescricao());
    		}

    		params.put("status", status.toString());

    		return params;

    	}

	private void recuperarListaCensoDiario() throws ApplicationBusinessException {

		voCensoDiario = pesquisaInternacaoFacade.pesquisarCensoDiarioPacientesSemPaginator(unidadeFuncionalSeq,
						unidadeFuncionalPai, data, status);

		recuperarUnidadeFuncional();

	}
	
	private void recuperarUnidadeFuncional() {
		unidadeFuncional = pesquisaInternacaoFacade.obterUnidadeFuncional(unidadeFuncionalSeq, unidadeFuncionalPai);

	}

	public Collection<VAinCensoVO> recuperarColecao() throws ApplicationBusinessException {

		if (unidadeFuncionalSeq != null && unidadeFuncionalPai != null) {

			unidadeFuncionalPai = null;

		}
		voCensoDiario = pesquisaInternacaoFacade
				.pesquisarCensoDiarioPacientesSemPaginator(unidadeFuncionalSeq, unidadeFuncionalPai, data, status);

		pesquisaInternacaoFacade.obterUnidadeFuncional(unidadeFuncionalSeq, unidadeFuncionalPai);

		return voCensoDiario;
	}   

	public String voltar() {
		unidadeFuncionalSeq = null;
		unidadeFuncionalPai = null;
		status = null;
		data = null;
		
		return VOLTAR_CENSO_DIARIO;
	}
             

	public void renderPdf(OutputStream out, Object data) throws ApplicationBusinessException, 
	IOException, JRException, DocumentException {

		DocumentoJasper documento = gerarDocumento();

		out.write(documento.getPdfByteArray(false)); // Protegido? = FALSE
	}
        

	public void directPrint(){

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);

		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}     

	public Short getUnidadeFuncionalSeq() {
		return unidadeFuncionalSeq;

	}

	public void setUnidadeFuncionalSeq(Short unidadeFuncionalSeq) {
		this.unidadeFuncionalSeq = unidadeFuncionalSeq;

	}

	public Date getData() {
		return data;

	}

	public void setData(Date data) {
		this.data = data;

	}

	public DominioSituacaoUnidadeFuncional getStatus() {
		return status;

	}

	public void setStatus(DominioSituacaoUnidadeFuncional status) {
		this.status = status;

	}

	public Short getUnidadeFuncionalPai() {
		return unidadeFuncionalPai;

	}

	public void setUnidadeFuncionalPai(Short unidadeFuncionalPai) {
		this.unidadeFuncionalPai = unidadeFuncionalPai;

	}
}