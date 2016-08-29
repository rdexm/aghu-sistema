package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.MpmModeloBasicoCuidado;
import br.gov.mec.aghu.model.MpmModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.prescricaomedica.modelobasico.vo.ItensModeloBasicoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.vo.ParametrosTelaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterItensModeloBasicoController extends ActionController {

	private static final long serialVersionUID = -4125733006787339276L;

	private static final String PAGE_MANTER_MODELO_BASICO = "manterModeloBasico";
	private static final String PAGE_MANTER_DIETA_MODELO_BASICO = "manterDietaModeloBasico";
	private static final String PAGE_MANTER_CUIDADO_MODELO_BASICO = "manterCuidadoModeloBasico";
	private static final String PAGE_MANTER_MEDICAMENTO_MODELO_BASICO = "manterMedicamentoModeloBasico";
	private static final String PAGE_MANTER_SOLUCOES_MODELO_BASICO = "manterSolucoesModeloBasico";
	private static final String PAGE_MANTER_PROCEDIMENTOS_MODELO_BASICO = "manterProcedimentoModeloBasico";

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;

	@Inject
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private RapServidores servidor;

	private Integer seqItemModelo;
	private Integer seqModelo;
	private String descricaoModeloBasico;
	private List<ItensModeloBasicoVO> itensModeloBasicoVO;
	private boolean modeloPertenceServidor = false;
	private Object object;
	private String tipo;

	private MpmModeloBasicoPrescricao modeloBasico;
	
	private ParametrosTelaVO parametrosTela;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	/**
	 * Recebe a seq do modelo basico e delega a pesquisa de itens para a ON.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void iniciar() throws ApplicationBusinessException {
	 

		if (seqModelo != null) {
			modeloBasico = this.modeloBasicoFacade.obterModeloBasicoPrescricaoComServidorPorId(seqModelo);

			if (modeloBasico != null) {
				// Verifica se o modelo basico pertence ao servidor logado no
				// sistema
				if (this.modeloBasico.getServidor() != null) {
					this.servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());

					if (this.modeloBasico.getServidor().equals(servidor)) {
						modeloPertenceServidor = true;
					}
				}

				itensModeloBasicoVO = this.modeloBasicoFacade.obterListaItensModelo(seqModelo);
			}

		} else {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SEQ_MODELO_NAO_INFORMADA");
		}
	
	}

	/**
	 * Testa o tipo de item que vai ser editado e redireciona para página
	 * responsável
	 * 
	 * @param seqItemModelo
	 * @return
	 */
	public String editarItens(Integer seqItemModelo, Integer seqModelo, String tipo) {
		this.seqModelo = seqModelo;
		this.seqItemModelo = seqItemModelo;

		if (ItensModeloBasicoVO.Tipo.DIETA.toString().equals(tipo)) {
			return PAGE_MANTER_DIETA_MODELO_BASICO;

		} else if (ItensModeloBasicoVO.Tipo.CUIDADO.toString().equals(tipo)) {
			return PAGE_MANTER_CUIDADO_MODELO_BASICO;

		} else if (ItensModeloBasicoVO.Tipo.MEDICAMENTO.toString().equals(tipo)) {
			return PAGE_MANTER_MEDICAMENTO_MODELO_BASICO;

		} else if (ItensModeloBasicoVO.Tipo.SOLUCAO.toString().equals(tipo)) {
			return PAGE_MANTER_SOLUCOES_MODELO_BASICO;

		} else if (ItensModeloBasicoVO.Tipo.PROCEDIMENTO.toString().equals(tipo)) {
			return PAGE_MANTER_PROCEDIMENTOS_MODELO_BASICO;
		}
		return null;
	}

	public String manterItensDieta() {
		this.seqItemModelo = null;
		return PAGE_MANTER_DIETA_MODELO_BASICO;
	}

	public String manterItensCuidado() {
		this.seqItemModelo = null;
		return PAGE_MANTER_CUIDADO_MODELO_BASICO;
	}

	public String manterItensMedicamento() {
		this.seqItemModelo = null;
		return PAGE_MANTER_MEDICAMENTO_MODELO_BASICO;
	}

	public String manterItensSolucao() {
		this.seqItemModelo = null;
		return PAGE_MANTER_SOLUCOES_MODELO_BASICO;
	}

	public String manterProcedimento() {
		this.seqItemModelo = null;
		return PAGE_MANTER_PROCEDIMENTOS_MODELO_BASICO;
	}

	/**
	 * Exclui modelo básico e seus itens.
	 */
	public void excluir() {
		try {
			if (modeloPertenceServidor) {
				if (ItensModeloBasicoVO.Tipo.DIETA.toString().equals(this.tipo)) {

					object = this.modeloBasicoFacade.obterModeloBasicoDieta(this.seqModelo, this.seqItemModelo);
					MpmModeloBasicoDieta modeloBasicoDieta = (MpmModeloBasicoDieta) object;
					String descricao = this.modeloBasicoFacade.getDescricaoEditadaDieta(modeloBasicoDieta);
					this.modeloBasicoFacade.excluir(object);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ITEM_DIETA", descricao);

				} else if (ItensModeloBasicoVO.Tipo.CUIDADO.toString().equals(this.tipo)) {

					object = this.modeloBasicoFacade.obterModeloBasicoCuidado(seqModelo, seqItemModelo);
					String descricao = this.modeloBasicoFacade.obterDescricaoEditadaModeloBasicoCuidado((MpmModeloBasicoCuidado) object);
					this.modeloBasicoFacade.excluir(object);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ITEM_CUIDADO", descricao);

				} else if (ItensModeloBasicoVO.Tipo.MEDICAMENTO.toString().equals(this.tipo) || ItensModeloBasicoVO.Tipo.SOLUCAO.toString().equals(this.tipo)) {

					object = this.modeloBasicoFacade.obterModeloBasicoMedicamento(seqModelo, seqItemModelo);
					MpmModeloBasicoMedicamento modeloBasicoMedicamento = (MpmModeloBasicoMedicamento) object;
					String texto = this.modeloBasicoFacade.getDescricaoEditadaMedicamentoItem(modeloBasicoMedicamento);
					this.modeloBasicoFacade.excluir(object);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ITEM_" + this.tipo.toUpperCase(), texto);

				} else if (ItensModeloBasicoVO.Tipo.PROCEDIMENTO.toString().equals(this.tipo)) {

					object = this.modeloBasicoFacade.obterModeloBasicoProcedimento(seqModelo, seqItemModelo);
					MpmModeloBasicoProcedimento modeloBasicoProcedimento = (MpmModeloBasicoProcedimento) object;
					String descricao = this.modeloBasicoFacade.getDescricaoEditadaProcedimento(modeloBasicoProcedimento);
					this.modeloBasicoFacade.excluir(object);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ITEM_PROCEDIMENTO", descricao);
				}

				this.itensModeloBasicoVO = this.modeloBasicoFacade.obterListaItensModelo(seqModelo);

			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_MODELO_NAO_PERTENCE_SERVIDOR");
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String cancelar() {
		return PAGE_MANTER_MODELO_BASICO;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public List<ItensModeloBasicoVO> getItensModeloBasicoVO() {
		return itensModeloBasicoVO;
	}

	public void setItensModeloBasicoVO(List<ItensModeloBasicoVO> itensModeloBasicoVO) {
		this.itensModeloBasicoVO = itensModeloBasicoVO;
	}

	public String getDescricaoModeloBasico() {
		return descricaoModeloBasico;
	}

	public void setDescricaoModeloBasico(String descricaoModeloBasico) {
		this.descricaoModeloBasico = descricaoModeloBasico;
	}

	public MpmModeloBasicoPrescricao getModeloBasico() {
		return modeloBasico;
	}

	public void setModeloBasico(MpmModeloBasicoPrescricao modeloBasico) {
		this.modeloBasico = modeloBasico;
	}

	public Integer getSeqModelo() {
		return seqModelo;
	}

	public void setSeqModelo(Integer seqModelo) {
		this.seqModelo = seqModelo;
	}

	public Integer getSeqItemModelo() {
		return seqItemModelo;
	}

	public void setSeqItemModelo(Integer seqItemModelo) {
		this.seqItemModelo = seqItemModelo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public ParametrosTelaVO getParametrosTela() {
		return parametrosTela;
	}
	
	public void setParametrosTela(ParametrosTelaVO parametrosTela) {
		this.parametrosTela = parametrosTela;
	}
	
}
