package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;

import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.RegistrarExameFisicoVO;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.McoRecemNascidosId;
import br.gov.mec.aghu.model.McoSindrome;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da aba Ex Fisico
 * 
 * @author felipe.rocha
 */
public class RegistrarGestacaoAbaExtFisicoRNController extends ActionController {


	private static final long serialVersionUID = -1712613170642413556L;
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	RegistrarGestacaoController registrarGestacaoController;
	@Inject
	CalculoCapurroController calculoCapurroController;
	
	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";
	
	private static final Integer ABA_GESTACAO_ATUAL = 0;
	
	private boolean modoEdicao = Boolean.FALSE;

	private McoSindrome mcoSindrome;

	private RegistrarExameFisicoVO vo;

	private RegistrarExameFisicoVO voAuxiliar;
	
	private RegistrarExameFisicoVO selecionado;

	private Integer pacCodigo;

	private Short gsoSeqp;

	private boolean itemExcluido;

	private boolean mostraModalGravarExFisicoRN;

	private List<RegistrarExameFisicoVO> dataModel = new ArrayList<RegistrarExameFisicoVO>();

	private static final String REDIRECIONA_PESQUISAR_GESTACOES = "pesquisaGestacoesList";
	
	private static final String REDIRECIONA_SNAPPE="preencherSnappe";
	
	private static final String REDIRECIONA_CAPURRO="realizarCalculoCapurro";

	public void reiniciarPesquisa() {
		try {
			setModoEdicao(Boolean.FALSE);
			limparVo();
			dataModel = emergenciaFacade.obterExameFisicoRN(getPacCodigo(),getGsoSeqp());
			selecionarPrimeiroItem();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void selecionarPrimeiroItem() {
		if (dataModel != null && dataModel.size() > 0) {
			if (getDataModel().get(0) != null) {
				vo.setProntuario(getDataModel().get(0).getProntuario());
				vo.setNome(getDataModel().get(0).getNome());
				vo.setSeq(getDataModel().get(0).getSeq());
				vo.setSeqp(getDataModel().get(0).getSeqp());
				vo.setDtHrExameFisico(getDataModel().get(0).getDtHrExameFisico());
				vo.setIdadeGestacionalFinal(getDataModel().get(0).getIdadeGestacionalFinal());
				vo.setAdqPeso(getDataModel().get(0).getAdqPeso());
				vo.setAspGeral(getDataModel().get(0).getAspGeral());
				vo.setSdm(getDataModel().get(0).getSdm());
				vo.setMoro(getDataModel().get(0).getMoro());
				vo.setSuccao(getDataModel().get(0).getSuccao());
				vo.setFugaAsfixia(getDataModel().get(0).getFugaAsfixia());
				vo.setReptacao(getDataModel().get(0).getReptacao());
				vo.setMarcha(getDataModel().get(0).getMarcha());
				vo.setCrede(getDataModel().get(0).getCrede());
				vo.setReflexoVermelho(getDataModel().get(0).getReflexoVermelho());
				vo.setSaturacaoDiferencial(getDataModel().get(0).getSaturacaoDiferencial());
				vo.setTempoRetornoMae(getDataModel().get(0).getTempoRetornoMae());
				vo.setTempoRetornoMin(getDataModel().get(0).getTempoRetornoMin());
				vo.setSemParticularidade(getDataModel().get(0).getSemParticularidade());
				vo.setObservacao(getDataModel().get(0).getObservacao());
				vo.setPacCodigo(getDataModel().get(0).getPacCodigo());				
			}
		}
	}

	private void limparVo() {
		vo = null;
		vo = new RegistrarExameFisicoVO();
	}

	private void copiarPropriedades() throws ReflectiveOperationException {

		PropertyUtils.copyProperties(getVo(), getVoAuxiliar());

	}

	/**
	 * Editar item
	 * @throws ReflectiveOperationException 
	 */
	public void editar() throws ReflectiveOperationException {
		setModoEdicao(Boolean.TRUE);
		if (voAuxiliar != null) {
			limparVo();
			copiarPropriedades();
		}
	}

	/**
	 * Cancelar Edição
	 */
	public void cancelarEdicao() {
		setModoEdicao(Boolean.FALSE);
		int indice = dataModel.indexOf(getVo());
		voAuxiliar = dataModel.get(indice);
		limparVo();
		selecionarItemIndice(indice);
	}

	/**
	 * Excluir
	 */
	public void excluir() {
		try {
			emergenciaFacade.validarExclusao(getPacCodigo(), getGsoSeqp(), getVoAuxiliar());
			getDataModel().remove(getVoAuxiliar());
			if (getDataModel() != null && getDataModel().size() > 0) {
				selecionarPrimeiroItem();
			} else {
				limparVo();
			}
			setModoEdicao(Boolean.FALSE);
			setItemExcluido(Boolean.TRUE);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Alterar
	 */
	public void alterar() {
		if (!validarCampoObrigatorio(getVo())) {
			try {
				int indice = dataModel.indexOf(getVo());
				voAuxiliar = dataModel.get(indice);
				vo = emergenciaFacade.validarRegistroExameFisico(getVo());
				boolean alterou = emergenciaFacade.alterarRegistroExameFisico(getVo(), getVoAuxiliar());
				if (alterou) {
					vo.setAlterouItem(true);
					// suggestion
					if (getMcoSindrome() != null && getMcoSindrome().getSeq() != null) {
						vo.setSdm(getMcoSindrome().getSeq());
					}
					dataModel.set(indice, getVo());
				}
				limparVo();
				selecionarItemIndice(indice);
				setModoEdicao(Boolean.FALSE);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);

			}
		}

	}

	private boolean validarCampoObrigatorio(RegistrarExameFisicoVO vo) {
		if (vo.getMoro() == null) {
			this.apresentarMsgNegocio("moro", Severity.ERROR,
					CAMPO_OBRIGATORIO, "Moro");
			return true;
		}
		if (vo.getFugaAsfixia() == null) {
			this.apresentarMsgNegocio("fuga_asfixia", Severity.ERROR,
					CAMPO_OBRIGATORIO, "Fuga Asfixia");
			return true;
		}
		if (vo.getReptacao() == null) {
			this.apresentarMsgNegocio("reptacao", Severity.ERROR,
					CAMPO_OBRIGATORIO, "Reptação");
			return true;
		}
		if (vo.getMarcha() == null) {
			this.apresentarMsgNegocio("marcha", Severity.ERROR,
					CAMPO_OBRIGATORIO, "Marcha");
			return true;
		}
		if (vo.getSuccao() == null) {
			this.apresentarMsgNegocio("succao", Severity.ERROR,
					CAMPO_OBRIGATORIO, "Sucção");
			return true;
		}

		return false;
	}

	/**
	 * Validar se existem itens alterados na tela
	 * 
	 * @return
	 */
	public boolean validarAlteracaoItens() {
		if (isItemExcluido()) {
			return true;
		}
		for (RegistrarExameFisicoVO registrarExameFisicoVO : dataModel) {
			if (registrarExameFisicoVO.isAlterouItem()) {
				return true;
			}
		}
		if (vo != null && isModoEdicao()) {
			int index = dataModel.indexOf(vo);
			voAuxiliar = dataModel.get(index);
			boolean alterou = emergenciaFacade.alterarRegistroExameFisico(getVo(), getVoAuxiliar());
			return alterou;

		}
		return false;
	}

	/**
	 * Selecionar Item
	 */
	public void selecionarItem() {
		if (voAuxiliar != null) {
			limparVo();
			vo.setProntuario(voAuxiliar.getProntuario());
			vo.setNome(voAuxiliar.getNome());
			vo.setSeq(voAuxiliar.getSeq());
			vo.setSeqp(voAuxiliar.getSeqp());
			vo.setDtHrExameFisico(voAuxiliar.getDtHrExameFisico());
			vo.setIdadeGestacionalFinal(voAuxiliar.getIdadeGestacionalFinal());
			vo.setAdqPeso(voAuxiliar.getAdqPeso());
			vo.setAspGeral(voAuxiliar.getAspGeral());
			vo.setSdm(voAuxiliar.getSdm());
			vo.setMoro(voAuxiliar.getMoro());
			vo.setSuccao(voAuxiliar.getSuccao());
			vo.setFugaAsfixia(voAuxiliar.getFugaAsfixia());
			vo.setReptacao(voAuxiliar.getReptacao());
			vo.setMarcha(voAuxiliar.getMarcha());
			vo.setCrede(voAuxiliar.getCrede());
			vo.setReflexoVermelho(voAuxiliar.getReflexoVermelho());
			vo.setSaturacaoDiferencial(voAuxiliar.getSaturacaoDiferencial());
			vo.setTempoRetornoMae(voAuxiliar.getTempoRetornoMae());
			vo.setTempoRetornoMin(voAuxiliar.getTempoRetornoMin());
			vo.setSemParticularidade(voAuxiliar.getSemParticularidade());
			vo.setObservacao(voAuxiliar.getObservacao());
			vo.setPacCodigo(voAuxiliar.getPacCodigo());
			// carregar suggestion
			if (vo.getSdm() != null) {
				mcoSindrome = emergenciaFacade.obterSindromePorChavePrimaria(vo.getSdm());
			}

		}
	}

	/**
	 * Selecionar Item Indice
	 */
	public void selecionarItemIndice(int indice) {
		if (dataModel != null && getDataModel().get(indice) != null) {
			vo.setProntuario(getDataModel().get(indice).getProntuario());
			vo.setNome(getDataModel().get(indice).getNome());
			vo.setSeq(getDataModel().get(indice).getSeq());
			vo.setSeqp(getDataModel().get(indice).getSeqp());
			vo.setDtHrExameFisico(getDataModel().get(indice).getDtHrExameFisico());
			vo.setIdadeGestacionalFinal(getDataModel().get(indice).getIdadeGestacionalFinal());
			vo.setAdqPeso(getDataModel().get(indice).getAdqPeso());
			vo.setAspGeral(getDataModel().get(indice).getAspGeral());
			vo.setSdm(getDataModel().get(indice).getSdm());
			vo.setMoro(getDataModel().get(indice).getMoro());
			vo.setSuccao(getDataModel().get(indice).getSuccao());
			vo.setFugaAsfixia(getDataModel().get(indice).getFugaAsfixia());
			vo.setReptacao(getDataModel().get(indice).getReptacao());
			vo.setMarcha(getDataModel().get(indice).getMarcha());
			vo.setCrede(getDataModel().get(indice).getCrede());
			vo.setReflexoVermelho(getDataModel().get(indice).getReflexoVermelho());
			vo.setSaturacaoDiferencial(getDataModel().get(indice).getSaturacaoDiferencial());
			vo.setTempoRetornoMae(getDataModel().get(indice).getTempoRetornoMae());
			vo.setTempoRetornoMin(getDataModel().get(indice).getTempoRetornoMin());
			vo.setSemParticularidade(getDataModel().get(indice).getSemParticularidade());
			vo.setObservacao(getDataModel().get(indice).getObservacao());

			// carregar suggestion
			if (vo.getSdm() != null) {
				mcoSindrome = emergenciaFacade.obterSindromePorChavePrimaria(vo.getSdm());
			}
		}

	}

	/**
	 * Voltar
	 * 
	 * @return
	 */
	public String voltar() {
		if (!validarAlteracaoItens()) {
			return descartarAlteracoes();
		} else {
			setMostraModalGravarExFisicoRN(Boolean.TRUE);
		}
		return null;
	}
	
	
	public String redirecionarSnappe(){
		return REDIRECIONA_SNAPPE;
	}
	
	public String redirecionarCalculo() {
		McoRecemNascidosId id = new McoRecemNascidosId(this.pacCodigo, this.gsoSeqp, this.vo.getSeqp());
		McoRecemNascidos recemNascido = this.emergenciaFacade.obterRecemNascidoPorId(id);
		
		BigDecimal peso = pacienteFacade.obterPesoPacientesPorCodigoPaciente(recemNascido.getPaciente().getCodigo());
		if (peso != null) {
			peso = peso.multiply(BigDecimal.valueOf(1000));
		}
		if (peso.compareTo(BigDecimal.valueOf(1800)) > 0) {
			this.calculoCapurroController.setPacCodigo(recemNascido.getPaciente().getCodigo());
			return REDIRECIONA_CAPURRO;
		// TODO - Cálculo Ballard - #27484
		} else {
			return null;
		}
	}
	
	/**
	 *  Não da modal
	 * @return
	 */
	public String descartarAlteracoes() {
		reiniciarPesquisa();
		setItemExcluido(Boolean.FALSE);
		setMostraModalGravarExFisicoRN(Boolean.FALSE);
		if (registrarGestacaoController.getAbaDestino().equals("abaExFisicoRN")) {
			this.registrarGestacaoController.setAbaSelecionada(ABA_GESTACAO_ATUAL);
			return REDIRECIONA_PESQUISAR_GESTACOES;
		} else {
			registrarGestacaoController.preparaAbaDestino();
			return null;
		}
	}

	/**
	 * Método que recebe os parâmetros da tela
	 * 
	 * @param pacCodigo
	 * @param seqp
	 */
	public void prepararTela(Integer pacCodigo, Short gsoSeqp) {
		this.pacCodigo = pacCodigo;
		this.gsoSeqp = gsoSeqp;
		reiniciarPesquisa();
	}

	public Long pesquisarMcoSindromeCount(String objPesquisa) {
		return emergenciaFacade.listaMcoSindromeAtivaCount(objPesquisa);

	}

	public List<McoSindrome> pesquisarMcoSindrome(String objPesquisa) {
		return  this.returnSGWithCount(emergenciaFacade.listarMcoSindromeAtiva(objPesquisa),pesquisarMcoSindromeCount(objPesquisa));

	}

	public McoSindrome getMcoSindrome() {
		return mcoSindrome;
	}

	public void setMcoSindrome(McoSindrome mcoSindrome) {
		this.mcoSindrome = mcoSindrome;
	}

	public RegistrarExameFisicoVO getVo() {
		return vo;
	}

	public void setVo(RegistrarExameFisicoVO vo) {
		this.vo = vo;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public List<RegistrarExameFisicoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(List<RegistrarExameFisicoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public boolean isMostraModalGravarExFisicoRN() {
		return mostraModalGravarExFisicoRN;
	}

	public void setMostraModalGravarExFisicoRN(
			boolean mostraModalGravarExFisicoRN) {
		this.mostraModalGravarExFisicoRN = mostraModalGravarExFisicoRN;
	}

	public RegistrarExameFisicoVO getVoAuxiliar() {
		return voAuxiliar;
	}

	public void setVoAuxiliar(RegistrarExameFisicoVO voAuxiliar) {
		this.voAuxiliar = voAuxiliar;
	}

	public boolean isItemExcluido() {
		return itemExcluido;
	}

	public void setItemExcluido(boolean itemExcluido) {
		this.itemExcluido = itemExcluido;
	}

	public RegistrarExameFisicoVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(RegistrarExameFisicoVO selecionado) {
		this.selecionado = selecionado;
	}

}