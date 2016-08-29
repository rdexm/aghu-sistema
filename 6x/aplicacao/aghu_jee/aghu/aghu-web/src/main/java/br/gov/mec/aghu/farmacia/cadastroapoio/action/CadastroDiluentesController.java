package br.gov.mec.aghu.farmacia.cadastroapoio.action;


import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.vo.CadastroDiluentesVO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaVinculoDiluentes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.view.VAfaDescrMdto;



public class CadastroDiluentesController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

//	private static final Log LOG = LogFactory.getLog(CadastroDiluentesController.class);

	
	private static final long serialVersionUID = -5538829983667729559L;

	public static enum RetornoAcaoStringEnum {
			
		
			INFORMA_SUCESSO_INCLUSAO("MENSAGEM_SUCESSO_CRIACAO_DILUENTE"),
			INFORMA_SUCESSO_ALTERACAO("MENSAGEM_SUCESSO_EDICAO_DILUENTE"),
			INFORMA_SUCESSO_REMOCAO("MENSAGEM_SUCESSO_REMOCAO_DILUENTE"),
			INFORMA_ERRO_REMOCAO("MENSAGEM_ERRO_REMOCAO_DILUENTE"),
			CONFIRMADO("farmacia-confirmado"),
			CANCELADO("farmacia-cancelado"),
			ERRO("farmacia-erro");
			
			private final String str;
			
			RetornoAcaoStringEnum(String str) {
				
				this.str = str;
			}
			
			@Override
			public String toString() {
				
				return this.str;
			}
	}

	private AfaMedicamento medicamentoSelecionado;
	private AfaVinculoDiluentes diluente;
	private Integer matCodigo;
	private Integer seq;
	private Boolean isUpdate = Boolean.FALSE;
	private CadastroDiluentesVO diluenteVO;
	private VAfaDescrMdto diluenteSelecionado;
	private RapServidores servidorLogado;
	
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private CadastroDiluentesPaginatorController cadastroDiluentesPaginatorController;

	public void iniciarPagina() {
		this.setDiluente(instanciarVinculoDiluentes(this.getSeq(), this.getMedicamentoSelecionado()));
	}
	
	public AfaVinculoDiluentes instanciarVinculoDiluentes(Integer seq, AfaMedicamento medicamento) {
		
		AfaVinculoDiluentes result = null;

		if (getSeq() != null && this.getIsUpdate()) {
			result = this.farmaciaFacade.obterDiluente(seq);
			for(VAfaDescrMdto diSelecionado : farmaciaFacade.obtemListaDiluentes(result.getDiluente().getMatCodigo())){
				setDiluenteSelecionado(diSelecionado);
			}
		} else {
			result = new AfaVinculoDiluentes();
			result.setSeq(null);
			result.setMedicamento(medicamento); 
			result.setIndSituacao(DominioSituacao.A);
		}
		
		return result;
	}
	
	public void reiniciarPaginatorController() {
		cadastroDiluentesPaginatorController.getDataModel().reiniciarPaginator();
	}

	public boolean validarInclusao() {

		return this.getSeq() == null && !this.getIsUpdate();
	}

	public String confirmar() {
		reiniciarPaginatorController();
		String result = RetornoAcaoStringEnum.ERRO.toString();
		
		try {	
			this.servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),new Date());
			
			result = RetornoAcaoStringEnum.CONFIRMADO.toString();
			if (this.validarInclusao()) {
				this.farmaciaFacade.efetuarInclusaoDiluente(diluente, servidorLogado, this.diluenteSelecionado, this.medicamentoSelecionado.getMatCodigo());
				this.informarInclusaoSucesso(diluente);
			} else {
				this.farmaciaFacade.efetuarAlteracaoDiluente(diluente,servidorLogado, this.diluenteSelecionado, this.medicamentoSelecionado.getMatCodigo());
				this.informarAlteracaoSucesso(diluente);
			}
			limparDadosIniciais();
		}  catch (BaseException e2) {
			this.apresentarExcecaoNegocio(e2);
			result = RetornoAcaoStringEnum.ERRO.toString();
		} 
		
		return result;
	}
	
	public void excluir() {
		
		AfaVinculoDiluentes diluente = null;
		reiniciarPaginatorController();
			
			diluente = this.farmaciaFacade.obterDiluente(seq);
			if (diluente != null) {
				try {
					this.servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),new Date());
					this.farmaciaFacade.efetuarRemocao(diluente, this.servidorLogado);
					this.informarExclusaoSucesso(diluente);
					this.setSeq(null);
				} catch (BaseException e1) {
					this.apresentarExcecaoNegocio(e1);
				} catch (Exception e2) {
					this.informarExclusaoErro(diluente);
				}
			} else {
				this.informarExclusaoErro(null);
			}
	}
	
	public void informarExclusaoSucesso(AfaVinculoDiluentes remocaoDiluente) {
		
		this.apresentarMsgNegocio(Severity.INFO,
				RetornoAcaoStringEnum.INFORMA_SUCESSO_REMOCAO.toString(),
				remocaoDiluente.getDiluente().getDescricao());		
	}
	
	public void informarExclusaoErro(AfaVinculoDiluentes erroRemocaoDiluente) {
		
		this.apresentarMsgNegocio(Severity.INFO,
				RetornoAcaoStringEnum.INFORMA_ERRO_REMOCAO.toString());
	}
	
	public void limpar() {
		iniciarPagina();
	}
	
	public void limparDadosIniciais() {
		setDiluenteSelecionado(null);
		setSeq(null);
	}
	
	public String cancelar() {
		return RetornoAcaoStringEnum.CANCELADO.toString();
	}
	
	public DominioSituacao[] obterValoresDominioSituacao() {
		return DominioSituacao.values();
	}
	
	public List<VAfaDescrMdto> buscarDiluentes(){
		return this.farmaciaFacade.obtemListaDiluentes();
	}
	
	public void informarInclusaoSucesso(AfaVinculoDiluentes inclusaoDiluente) {
		
		this.apresentarMsgNegocio(Severity.INFO,
				RetornoAcaoStringEnum.INFORMA_SUCESSO_INCLUSAO.toString(),
				inclusaoDiluente.getDiluente().getDescricao());
	}
	
	public void informarAlteracaoSucesso(AfaVinculoDiluentes alteracaoDiluente) {
		
		this.apresentarMsgNegocio(Severity.INFO,
				RetornoAcaoStringEnum.INFORMA_SUCESSO_ALTERACAO.toString(),
				alteracaoDiluente.getDiluente().getDescricao());
	}

	
	public AfaMedicamento getMedicamentoSelecionado() {
		return medicamentoSelecionado;
	}

	public void setMedicamentoSelecionado(AfaMedicamento medicamentoSelecionado) {
		this.medicamentoSelecionado = medicamentoSelecionado;
	}

	public AfaVinculoDiluentes getDiluente() {
		return diluente;
	}


	public void setDiluente(AfaVinculoDiluentes diluente) {
		this.diluente = diluente;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public Boolean getIsUpdate() {
		return isUpdate;
	}
	
	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public Integer getSeq() {
		return seq;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public CadastroDiluentesVO getDiluenteVO() {
		return diluenteVO;
	}
	
	public void setDiluenteVO(CadastroDiluentesVO diluenteVO) {
		this.diluenteVO = diluenteVO;
	}

	public VAfaDescrMdto getDiluenteSelecionado() {
		return diluenteSelecionado;
	}

	public void setDiluenteSelecionado(VAfaDescrMdto diluenteSelecionado) {
		this.diluenteSelecionado = diluenteSelecionado;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

}