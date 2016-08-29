package br.gov.mec.aghu.internacao.pesquisa.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.vo.DadosInternacaoVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ExtratoPacienteVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaExtratoPacientePaginatorController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = 8359671554828167966L;
	
	private static final Log LOG = LogFactory.getLog(PesquisaExtratoPacientePaginatorController.class);

    private static final String ENCERRAMENTO_PREVIA_CONTA = "faturamento-encerramentoPreviaConta";
    private static final String PESQUISA_CENSO_DIARIO_PACIENTES = "internacao-pesquisarCensoDiarioPacientes";

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	private Date dataInternacao;
	private String dataHoraInternacao;
	private Integer prontuario;
	private String nomePaciente;
	private Short codConvenio;
	private Byte codPlano;
	private String descricaoConvenioPlano;

	private List<DadosInternacaoVO> listaDadosInternacao = new ArrayList<DadosInternacaoVO>();
	private int posicaoData;

	private boolean renderDetalhes = false;
	private boolean desabilitaAvancar;
	private boolean desabilitaVoltar;

	private ExtratoPacienteVO item;

	private String cameFrom;
	
	@Inject @Paginator
	private DynamicDataModel<ExtratoPacienteVO> dataModel;	

	@PostConstruct
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void inicio() {
		begin(conversation, true);

        // this.ordenacao = DominioOrdenacaoPesquisaPacComAlta.U;
		try {
			if (this.prontuario != null) {
				this.pesquisar();
			}
		} catch (Exception e) {
			LOG.error("Erro no @PostConstruct PesquisaExtratoPacientePaginatorController", e);
		}
	}

	/**
	 * Limpa os campos da tela.
	 */
	public void limparPesquisa() {
		this.listaDadosInternacao = new ArrayList<DadosInternacaoVO>();
		this.nomePaciente = null;
		this.codConvenio = null;
		this.codPlano = null;
		this.descricaoConvenioPlano = null;
		this.prontuario = null;
		this.dataInternacao = null;
		this.renderDetalhes = false;
		this.desabilitaAvancar = true;
		this.desabilitaVoltar = true;
		this.dataModel.limparPesquisa();
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela.
	 */
	public String cancelar() {
		if (PESQUISA_CENSO_DIARIO_PACIENTES.equalsIgnoreCase(cameFrom)) {
			this.limparPesquisa();
			return PESQUISA_CENSO_DIARIO_PACIENTES;
		} else if(ENCERRAMENTO_PREVIA_CONTA.equalsIgnoreCase(cameFrom)) {
			this.limparPesquisa();
            return ENCERRAMENTO_PREVIA_CONTA;
        }

		return cameFrom;
	}

	/**
	 * Método que realiza a ação do botão pesquisar.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void pesquisar() throws Exception {
		try {
			// verifica se o prontuario é nulo
			if (!this.pesquisaInternacaoFacade.validar(this.prontuario)) {
				apresentarMsgNegocio(Severity.ERROR, "ERRO_PRONTUARIO_NAO_INFORMADO"); 
			} 
				// se o paciente nao tiver nenhuma internacao retorna falso
				if (!this.preencherListaDatas()) {
					apresentarMsgNegocio(Severity.ERROR, "ERRO_PACIENTE_SEM_INTERNACAO"); 
				} else {

					if (this.listaDadosInternacao.get(posicaoData).getDataInternacao() != null) {
						this.setDataInternacao(this.listaDadosInternacao.get(posicaoData).getDataInternacao());
						SimpleDateFormat formata = new SimpleDateFormat("dd/MM/yyyy HH:mm");
						this.dataHoraInternacao = formata.format(this.listaDadosInternacao.get(posicaoData).getDataInternacao());
					}

					if (this.listaDadosInternacao.get(posicaoData) != null) {
						this.setCodPlano(this.listaDadosInternacao.get(posicaoData).getCodigoPlanoSaude());
						this.setCodConvenio(this.listaDadosInternacao.get(posicaoData).getCodigoConvenioPlano());
						this.setDescricaoConvenioPlano(this.listaDadosInternacao.get(posicaoData).getDescricaoConvenioPlanoSaude());
					}

					if (this.nomePaciente == null && this.prontuario != null) {
						this.setNomePaciente(this.pesquisaInternacaoFacade.getNomePacienteProntuario(this.prontuario));
					}

					if (this.listaDadosInternacao.size() > 1) {
						this.desabilitaAvancar = false;
						this.desabilitaVoltar = true;
					} else {
						this.desabilitaAvancar = true;
						this.desabilitaVoltar = true;
					}

					this.renderDetalhes = true;

					dataModel.reiniciarPaginator();
				}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			this.dataModel.limparPesquisa();
			this.renderDetalhes = false;
		}
	
	}
	public void detalharMovimento(ExtratoPacienteVO item) {
		this.setRenderDetalhes(true);
		this.setItem(item);
	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void avancar() throws Exception {
		if (this.posicaoData + 1 < this.listaDadosInternacao.size()) {
			this.posicaoData++;
			this.pesquisar();

			if (this.posicaoData + 1 >= this.listaDadosInternacao.size()) {
				this.desabilitaAvancar = true;
			} else {
				this.desabilitaAvancar = false;
			}
			this.desabilitaVoltar = false;

		}

	}

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void voltar() throws Exception {
		if (this.posicaoData - 1 >= 0) {
			this.posicaoData--;
			this.pesquisar();
			if (this.posicaoData - 1 < 0) {
				this.desabilitaVoltar = true;
			} else {
				this.desabilitaVoltar = false;
			}
			this.desabilitaAvancar = false;
		}

	}

	private boolean preencherListaDatas() {
		if (this.listaDadosInternacao.size() == 0) {
			this.listaDadosInternacao = this.pesquisaInternacaoFacade.pesquisarDatas(this.prontuario, this.dataInternacao);
			this.posicaoData = 0;
			if (this.listaDadosInternacao.size() == 0) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}

	}

	@Override
	public Long recuperarCount() {
		return this.pesquisaInternacaoFacade.pesquisarExtratoPacienteCount(this.listaDadosInternacao.get(posicaoData)
				.getCodigoInternacao(), this.listaDadosInternacao.get(posicaoData).getDataInternacao());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ExtratoPacienteVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		List<ExtratoPacienteVO> lista = this.pesquisaInternacaoFacade.pesquisarExtratoPaciente(firstResult, maxResult,
				this.listaDadosInternacao.get(posicaoData).getCodigoInternacao(), this.listaDadosInternacao.get(posicaoData)
						.getDataInternacao());

		return lista;
	}

	public Date getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public void setPosicaoData(int posicaoData) {
		this.posicaoData = posicaoData;
	}

	public int getPosicaoData() {
		return posicaoData;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setRenderDetalhes(boolean renderDetalhes) {
		this.renderDetalhes = renderDetalhes;
	}

	public boolean isRenderDetalhes() {
		return renderDetalhes;
	}

	public void setItem(ExtratoPacienteVO item) {
		this.item = item;
	}

	public ExtratoPacienteVO getItem() {
		return item;
	}

	public void setListaDadosInternacao(List<DadosInternacaoVO> listaDadosInternacao) {
		this.listaDadosInternacao = listaDadosInternacao;
	}

	public List<DadosInternacaoVO> getListaDadosInternacao() {
		return listaDadosInternacao;
	}

	public Short getCodConvenio() {
		return codConvenio;
	}

	public void setCodConvenio(Short codConvenio) {
		this.codConvenio = codConvenio;
	}

	public Byte getCodPlano() {
		return codPlano;
	}

	public void setCodPlano(Byte codPlano) {
		this.codPlano = codPlano;
	}

	public String getDescricaoConvenioPlano() {
		return descricaoConvenioPlano;
	}

	public void setDescricaoConvenioPlano(String descricaoConvenioPlano) {
		this.descricaoConvenioPlano = descricaoConvenioPlano;
	}

	public boolean isDesabilitaAvancar() {
		return desabilitaAvancar;
	}

	public void setDesabilitaAvancar(boolean desabilitaAvancar) {
		this.desabilitaAvancar = desabilitaAvancar;
	}

	public boolean isDesabilitaVoltar() {
		return desabilitaVoltar;
	}

	public void setDesabilitaVoltar(boolean desabilitaVoltar) {
		this.desabilitaVoltar = desabilitaVoltar;
	}

	public void setDataHoraInternacao(String dataHoraInternacao) {
		this.dataHoraInternacao = dataHoraInternacao;
	}

	public String getDataHoraInternacao() {
		return dataHoraInternacao;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public DynamicDataModel<ExtratoPacienteVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ExtratoPacienteVO> dataModel) {
		this.dataModel = dataModel;
	}

}
