package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoProcedimentoObstetricos;
import br.gov.mec.aghu.perinatologia.vo.IntercorrenciaNascsVO;
import br.gov.mec.aghu.perinatologia.vo.IntercorrenciaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class RegistrarGestacaoAbaNascimentoFieldsetIntercorrenciasController extends ActionController {
	private static final long serialVersionUID = -4858884771232547892L;

	@Inject
	private IEmergenciaFacade emergenciaFacade;

	@Inject
	private RegistrarGestacaoAbaNascimentoController registrarGestacaoAbaNascimentoController;

	// McoIntercorrenciaNascs selecionada na suggestionbox
	private IntercorrenciaVO intercorrenciaVO;
	// McoProcedimentoObstetricos selecionada na suggestionbox
	private McoProcedimentoObstetricos procedimentoObstetrico;
	// Data e Hora da intercorrencia na tela
	private Date dataHoraIntercorrencia;
	// Controle de tela;
	private boolean edicao;

	private Integer nasGsoPacCodigo = null;
	private Short nasGsoSeqp = null;
	private Integer nasSeqp = null;
	private Integer consulta = null;

	// Lista de McoIntercorrenciaNascs
	private List<IntercorrenciaNascsVO> listaIntercorrenciaNascsVO = new ArrayList<IntercorrenciaNascsVO>();

	// Item da lista selecionado para exclusao ou alteracao
	private IntercorrenciaNascsVO intercorrenciaNascsVOSelecionado;

	/* -------------------------------------------------------------------------------------- */

	/**
	 * Suggestion Intercorrência - PESQUISA
	 * 
	 * @param param
	 * @return
	 */
	public List<IntercorrenciaVO> pesquisarIntercorrencias(String param) {
		List<IntercorrenciaVO> result = new ArrayList<IntercorrenciaVO>();
		try {
			result = this.emergenciaFacade.pesquisarMcoIntercorrenciaAtivosPorSeqOuDescricao((String) param);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
		return  this.returnSGWithCount(result,pesquisarIntercorrenciasCount(param));
	}

	/**
	 * Suggestion Intercorrência - COUNT
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarIntercorrenciasCount(String param) {
		return this.emergenciaFacade.pesquisarMcoIntercorrenciaAtivosPorSeqOuDescricaoCount((String) param);
	}

	/**
	 * Suggestion Procedimentos - PESQUISA
	 * 
	 * @param param
	 * @return
	 */
	public List<McoProcedimentoObstetricos> pesquisarProcedimentos(String param) {
		String strParam = (String) param;
		Short seq = null;
		String descricao = null;
		if (StringUtils.isNotBlank(strParam)) {
			if (CoreUtil.isNumeroShort(strParam)) {
				seq = Short.valueOf(strParam);
			} else {
				descricao = strParam;
			}
		}
		return  this.returnSGWithCount(this.emergenciaFacade.pesquisarMcoProcedimentoObstetricos(0, 100, McoProcedimentoObstetricos.Fields.DESCRICAO.toString(),
				true, seq, descricao, null, DominioSituacao.A),pesquisarProcedimentosCount(param));
	}

	/**
	 * Suggestion Procedimentos - COUNT
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarProcedimentosCount(String param) {
		String strParam = (String) param;
		Short seq = null;
		String descricao = null;
		if (StringUtils.isNotBlank(strParam)) {
			if (CoreUtil.isNumeroShort(strParam)) {
				seq = Short.valueOf(strParam);
			} else {
				descricao = strParam;
			}
		}
		return this.emergenciaFacade.pesquisarMcoProcedimentoObstetricosCount(seq, descricao, null, DominioSituacao.A);
	}
	
	private boolean validarCamposObrigatorios() {
		boolean validou = true;
		if (this.dataHoraIntercorrencia == null) {
			this.apresentarMsgNegocio("dtHrIntercorrencia", Severity.ERROR, "CAMPO_OBRIGATORIO", "Data");
			validou = false;
		}
		if (this.intercorrenciaVO == null) {
			this.apresentarMsgNegocio("intercorrenciaVO", Severity.ERROR, "CAMPO_OBRIGATORIO", "Intercorrência");
			validou = false;
		}
		return validou;
	}

	/**
	 * Botão ADICIONAR
	 */
	public void adicionarIntercorrencia() {
		if (validarCamposObrigatorios()) {
			try {
				// Salva registro no banco
				IntercorrenciaNascsVO intercorrenciaNascsVO = this.emergenciaFacade.inserirMcoIntercorrenciaNascs(this.nasGsoPacCodigo,
						this.nasGsoSeqp, this.nasSeqp, this.consulta, this.dataHoraIntercorrencia, this.intercorrenciaVO,
						this.procedimentoObstetrico);
				
				// Adiciona resultado na tabela
				this.listaIntercorrenciaNascsVO.add(intercorrenciaNascsVO);
				
				// Limpa os campos de edição
				this.limparCamposEdicao();
				
				// Aprensenta mensagem de sucesso
				super.apresentarMsgNegocio("MENSAGEM_SUCESSO_CADASTRO_INTERCORRENCIA");
				
			} catch (ApplicationBusinessException e) {
				super.apresentarExcecaoNegocio(e);
			}
		}

	}

	/**
	 * Botão REMOVER
	 */
	public void removerIntercorrencia() {
		try {
			if (this.intercorrenciaNascsVOSelecionado != null) {

				// Remove registro do banco
				this.emergenciaFacade.removerMcoIntercorrenciaNascs(this.intercorrenciaNascsVOSelecionado);

				// Remove registro da tabela
				this.listaIntercorrenciaNascsVO.remove(this.intercorrenciaNascsVOSelecionado);

				// Aprensenta mensagem de sucesso
				super.apresentarMsgNegocio("MENSAGEM_SUCESSO_EXCLUSAO_INTERCORRENCIA");
			}
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Botão EDITAR
	 */
	public void editarIntercorrencia() {
		if (this.intercorrenciaNascsVOSelecionado != null) {

			// Marca para edição
			this.edicao = true;

			// Ajusta data e hora do registro selecionado
			this.dataHoraIntercorrencia = this.intercorrenciaNascsVOSelecionado.getMcoIntercorrenciaNascs().getDthrIntercorrencia();

			// Ajusta McoIntercorrencia do registro selecionado
			this.intercorrenciaVO = new IntercorrenciaVO(this.intercorrenciaNascsVOSelecionado.getCodigoCid(),
					this.intercorrenciaNascsVOSelecionado.getMcoIntercorrenciaNascs().getMcoIntercorrencia());

			// Ajusta McoProcedimentoObstetricos do registro selecionado
			this.procedimentoObstetrico = this.intercorrenciaNascsVOSelecionado.getMcoIntercorrenciaNascs().getMcoProcedimentoObstetricos();
		}
	}

	/**
	 * Botão ALTERAR
	 */
	public void alterarIntercorrencia() {

		if (this.intercorrenciaNascsVOSelecionado != null && validarCamposObrigatorios()) {
			try {

				// Altera registro no banco
				IntercorrenciaNascsVO intercorrenciaNascsVOAlterado = this.emergenciaFacade.alterarMcoIntercorrenciaNascs(this.consulta,
						this.intercorrenciaNascsVOSelecionado, this.dataHoraIntercorrencia, this.intercorrenciaVO,
						this.procedimentoObstetrico);

				// Ajusta o registro selecionado na tabela
				this.intercorrenciaNascsVOSelecionado.setCodigoCid(intercorrenciaNascsVOAlterado.getCodigoCid());
				this.intercorrenciaNascsVOSelecionado.setMcoIntercorrenciaNascs(intercorrenciaNascsVOAlterado.getMcoIntercorrenciaNascs());

				// Limpa os campos de edição
				this.limparCamposEdicao();

				super.apresentarMsgNegocio("MENSAGEM_SUCESSO_ALTERACAO_INTERCORRENCIA");

			} catch (ApplicationBusinessException e) {
				super.apresentarExcecaoNegocio(e);
			}
		}
	}

	/**
	 * Botão LIMPAR EDICAO
	 */
	public void limparCamposEdicao() {
		this.edicao = false;
		this.dataHoraIntercorrencia = null;
		this.intercorrenciaVO = null;
		this.procedimentoObstetrico = null;
		this.intercorrenciaNascsVOSelecionado = null;
	}

	
	/**
	 * Utilizado para inicializar o fieldset
	 */
	public void limparFieldset() {
		listaIntercorrenciaNascsVO.clear();
	}
	/* -------------------------------------------------------------------------------------- */

	/**
	 * Utilizado para inicializar o fieldset
	 */
	public void prepararFieldset(final Integer gsoPacCodigo, final Short gsoSeqp, final Integer seqp, final Integer consulta) {

		this.nasGsoPacCodigo = gsoPacCodigo;
		this.nasGsoSeqp = gsoSeqp;
		this.nasSeqp = seqp;
		this.consulta = consulta;

		try {
			listaIntercorrenciaNascsVO = emergenciaFacade.pesquisarMcoIntercorrenciaNascsPorNascimento(this.nasGsoPacCodigo,
					this.nasGsoSeqp, this.nasSeqp);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/* -------------------------------------------------------------------------------------- */

	public boolean isPermManterIntercorrenciaNascimento() {
		return registrarGestacaoAbaNascimentoController.isPermManterIntercorrenciaNascimento();
	}

	/* -------------------------------------------------------------------------------------- */

	public IntercorrenciaVO getIntercorrenciaVO() {
		return intercorrenciaVO;
	}

	public List<IntercorrenciaNascsVO> getListaIntercorrenciaNascsVO() {
		return listaIntercorrenciaNascsVO;
	}

	public void setListaIntercorrenciaNascsVO(List<IntercorrenciaNascsVO> listaIntercorrenciaNascsVO) {
		this.listaIntercorrenciaNascsVO = listaIntercorrenciaNascsVO;
	}

	public IntercorrenciaNascsVO getIntercorrenciaNascsVOSelecionado() {
		return intercorrenciaNascsVOSelecionado;
	}

	public void setIntercorrenciaNascsVOSelecionado(IntercorrenciaNascsVO intercorrenciaNascsVO) {
		this.intercorrenciaNascsVOSelecionado = intercorrenciaNascsVO;
	}

	public void setIntercorrenciaVO(IntercorrenciaVO intercorrenciaVO) {
		this.intercorrenciaVO = intercorrenciaVO;
	}

	public void setProcedimentoObstetrico(McoProcedimentoObstetricos procedimentoObstetrico) {
		this.procedimentoObstetrico = procedimentoObstetrico;
	}

	public McoProcedimentoObstetricos getProcedimentoObstetrico() {
		return procedimentoObstetrico;
	}

	public void setProcedimentoObstetricos(McoProcedimentoObstetricos procedimentoObstetrico) {
		this.procedimentoObstetrico = procedimentoObstetrico;
	}

	public Date getDataHoraIntercorrencia() {
		return dataHoraIntercorrencia;
	}

	public void setDataHoraIntercorrencia(Date dataHoraIntercorrencia) {
		this.dataHoraIntercorrencia = dataHoraIntercorrencia;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}
}