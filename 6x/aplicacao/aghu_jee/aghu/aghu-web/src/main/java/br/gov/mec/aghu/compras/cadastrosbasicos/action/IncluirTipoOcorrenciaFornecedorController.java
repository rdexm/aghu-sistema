package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoTipoOcorrForn;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class IncluirTipoOcorrenciaFornecedorController extends ActionController {

	private static final String CADASTRAR_TIPO_OCORRENCIA_FORNECEDOR = "cadastrarTipoOcorrenciaFornecedor";

	private static final long serialVersionUID = -7781706374954598794L;

	@EJB
	private IComprasFacade comprasFacade;
	
	private ScoTipoOcorrForn ocorrencia;
	private Short codigoOcorrencia;
	private boolean edicao;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio(){
	 

	 

		if(codigoOcorrencia != null){
			ocorrencia = comprasFacade.buscarOcorrenciaFornecedor(codigoOcorrencia);
			setEdicao(Boolean.TRUE);
		}else{
			ocorrencia = new ScoTipoOcorrForn();
			ocorrencia.setIndSituacao(DominioSituacao.A);
			setEdicao(Boolean.FALSE);
		}
	
	}
	
	
	public String gravar(){
		try {
			comprasFacade.inserirOcorrenciaFornecedor(ocorrencia);
			if(isEdicao()){
				this.apresentarMsgNegocio(Severity.INFO, "OCORRENCIA_FORNECEDOR_M2");
			}else{
				this.apresentarMsgNegocio(Severity.INFO, "OCORRENCIA_FORNECEDOR_M1");
			}
			return CADASTRAR_TIPO_OCORRENCIA_FORNECEDOR;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String cancelar(){
		return CADASTRAR_TIPO_OCORRENCIA_FORNECEDOR;
	}

	public void setCodigoOcorrencia(Short codigoOcorrencia) {
		this.codigoOcorrencia = codigoOcorrencia;
	}

	public Short getCodigoOcorrencia() {
		return codigoOcorrencia;
	}

	public void setOcorrencia(ScoTipoOcorrForn ocorrencia) {
		this.ocorrencia = ocorrencia;
	}

	public ScoTipoOcorrForn getOcorrencia() {
		return ocorrencia;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public boolean isEdicao() {
		return edicao;
	}
}
