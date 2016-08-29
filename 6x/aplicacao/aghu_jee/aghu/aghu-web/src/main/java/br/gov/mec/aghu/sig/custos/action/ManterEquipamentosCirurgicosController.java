package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.SigEquipamentoPatrimonio;
import br.gov.mec.aghu.patrimonio.IPatrimonioService;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoSistemaPatrimonioVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterEquipamentosCirurgicosController extends ActionController {

	private static final String ASSOCIAR_EQUIPAMENTO_CIRURGICO_LIST = "associarEquipamentoCirurgicoList";

	private static final Log LOG = LogFactory.getLog(ManterEquipamentosCirurgicosController.class);

	private static final long serialVersionUID = -1502017164335662570L;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	private MbcEquipamentoCirurgico equipamentoCirurgico;

	private Integer seqEquipamentoCirurgico;

	private EquipamentoSistemaPatrimonioVO equipamentoSelecionado;

	private List<SigEquipamentoPatrimonio> listEquipamento;

	private List<SigEquipamentoPatrimonio> listEquipamentoExclusao;

	private boolean integracaoPatrimonioOnline;

	private String codigoEquipamentoExclusao;

	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		this.equipamentoCirurgico = this.blocoCirurgicoFacade.obterEquipamentoCirurgicoByID(getSeqEquipamentoCirurgico().shortValue());
		this.listEquipamento = this.custosSigFacade.buscaEquipametosCirurgicos(this.equipamentoCirurgico);
		this.listEquipamentoExclusao = new ArrayList<SigEquipamentoPatrimonio>();
		this.verificaServicoPatrimonioEstaOnline();
	
	}

	public void excluir() {
		for (int i = 0; i < this.listEquipamento.size(); i++) {
			SigEquipamentoPatrimonio equipamento = this.listEquipamento.get(i);
			if (equipamento.getCodPatrimonio().equalsIgnoreCase(this.codigoEquipamentoExclusao)) {
				if (equipamento.getSeq() != null) {
					this.listEquipamentoExclusao.add(equipamento);
				}
				this.listEquipamento.remove(i);
				break;
			}
		}
	}

	public void adicionar() throws ApplicationBusinessException {
		SigEquipamentoPatrimonio equipamento = new SigEquipamentoPatrimonio();
		equipamento.setCodPatrimonio(this.equipamentoSelecionado.getCodigo());
		equipamento.setCriadoEm(new Date());
		equipamento.setServidorResp(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		equipamento.setMbcEquipamentoCirurgico(this.equipamentoCirurgico);
		
		List<SigEquipamentoPatrimonio> listEquipamentoPatrimonio = this.custosSigFacade
				.pesquisarEquipamentoPatrimonioPeloCodgioPatrimonio(equipamento.getCodPatrimonio());
		if(listEquipamentoPatrimonio != null && !listEquipamentoPatrimonio.isEmpty()) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_EQUIPAMENTO_ASSOCIADO_M03",
					listEquipamentoPatrimonio.get(0).getMbcEquipamentoCirurgico().getDescricao());
			return;
		}

		for (SigEquipamentoPatrimonio patrimonio : this.listEquipamento) {
			if (patrimonio.getCodPatrimonio().equalsIgnoreCase(equipamento.getCodPatrimonio())) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_EQUIPAMENTO_ASSOCIADO_M02");
				return;
			}
		}
	
		if(listEquipamentoExclusao.contains(equipamento)){
			listEquipamentoExclusao.remove(equipamento);
		}
		this.listEquipamento.add(equipamento);
		this.equipamentoSelecionado = null;
	}

	public void gravar() {
		//Alterações e Inclusoes		
		for (SigEquipamentoPatrimonio adicao : this.listEquipamento) {
			if(adicao.getSeq() == null) {
				custosSigFacade.persistirEquipamentoCirurgico(adicao);
			}
		}
		for (SigEquipamentoPatrimonio excluido : this.listEquipamentoExclusao) {
			custosSigFacade.excluirEquipamentoCirurgico(excluido);
		}
		this.listEquipamentoExclusao.clear();
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_EQUIPAMENTO_CIRURGICO", this.equipamentoCirurgico.getDescricao());
	}
	
	public String voltar() {
		this.equipamentoCirurgico = null;
		this.listEquipamento = null;
		this.listEquipamentoExclusao = null;
		this.equipamentoSelecionado = null;
		return ASSOCIAR_EQUIPAMENTO_CIRURGICO_LIST;
	}

	public List<EquipamentoSistemaPatrimonioVO> pesquisarEquipamento(String paramPesquisa) throws BaseException {
		List<EquipamentoSistemaPatrimonioVO> lista = this.custosSigFacade.pesquisarEquipamentoSistemaPatrimonio(paramPesquisa, null);
		if (CoreUtil.isNumeroInteger(paramPesquisa)) {
			EquipamentoSistemaPatrimonioVO equipamento = lista.get(0);
			if (equipamento.getRetorno().intValue() == IPatrimonioService.RETORNO_INFO_PATRIMONIO_EQUIPAMENTO_NAO_ENCONTRADO) {
				lista = new ArrayList<EquipamentoSistemaPatrimonioVO>();
			}
			if (equipamento.getRetorno().intValue() == IPatrimonioService.RETORNO_INFO_PATRIMONIO_EQUIPAMENTO_PERTENCE_OUTRO_CC) {
				lista = new ArrayList<EquipamentoSistemaPatrimonioVO>();
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVICO_PATRIMONIO_EQUIPAMENTO_OUTRO_CC");
			}
		}
		return lista;
	}

	private EquipamentoSistemaPatrimonioVO buscaEquipamentoModuloPatrimonio(SigEquipamentoPatrimonio equipamento) {
		try {
			Integer centroCustoUniversal = 0;
			return this.custosSigFacade.pesquisarEquipamentoSistemaPatrimonioById(equipamento.getCodPatrimonio(), centroCustoUniversal);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVICO_PATRIMONIO_FALHA");
			if (this.integracaoPatrimonioOnline) {
				verificaServicoPatrimonioEstaOnline();
			}
			return null;
		}
	}

	private void verificaServicoPatrimonioEstaOnline() {
		try {
			String codigo = "0000";
			Integer cc = 0;
			this.custosSigFacade.pesquisarEquipamentoSistemaPatrimonioById(codigo, cc);
			this.setIntegracaoPatrimonioOnline(Boolean.TRUE);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVICO_PATRIMONIO_FORA");
			setIntegracaoPatrimonioOnline(Boolean.FALSE);
		}
	}

	public String getDescricaoBem(SigEquipamentoPatrimonio equipamento) {
		EquipamentoSistemaPatrimonioVO equipamentoVo = this.buscaEquipamentoModuloPatrimonio(equipamento);
		if (equipamentoVo == null) {
			return null;
		} else {
			return equipamentoVo.getDescricao();
		}
	}

	public MbcEquipamentoCirurgico getEquipamentoCirurgico() {
		return equipamentoCirurgico;
	}

	public void setEquipamentoCirurgico(MbcEquipamentoCirurgico equipamentoCirurgico) {
		this.equipamentoCirurgico = equipamentoCirurgico;
	}

	public EquipamentoSistemaPatrimonioVO getEquipamentoSelecionado() {
		return equipamentoSelecionado;
	}

	public void setEquipamentoSelecionado(EquipamentoSistemaPatrimonioVO equipamentoSelecionado) {
		this.equipamentoSelecionado = equipamentoSelecionado;
	}

	public Integer getSeqEquipamentoCirurgico() {
		return seqEquipamentoCirurgico;
	}

	public void setSeqEquipamentoCirurgico(Integer seqEquipamentoCirurgico) {
		this.seqEquipamentoCirurgico = seqEquipamentoCirurgico;
	}

	public List<SigEquipamentoPatrimonio> getListEquipamento() {
		return listEquipamento;
	}

	public void setListEquipamento(List<SigEquipamentoPatrimonio> listEquipamento) {
		this.listEquipamento = listEquipamento;
	}

	public boolean isIntegracaoPatrimonioOnline() {
		return integracaoPatrimonioOnline;
	}

	public void setIntegracaoPatrimonioOnline(boolean integracaoPatrimonioOnline) {
		this.integracaoPatrimonioOnline = integracaoPatrimonioOnline;
	}

	public List<SigEquipamentoPatrimonio> getListEquipamentoExclusao() {
		return listEquipamentoExclusao;
	}

	public void setListEquipamentoExclusao(List<SigEquipamentoPatrimonio> listEquipamentoExclusao) {
		this.listEquipamentoExclusao = listEquipamentoExclusao;
	}

	public String getCodigoEquipamentoExclusao() {
		return codigoEquipamentoExclusao;
	}

	public void setCodigoEquipamentoExclusao(String codigoEquipamentoExclusao) {
		this.codigoEquipamentoExclusao = codigoEquipamentoExclusao;
	}

}
