package br.gov.mec.aghu.exames.agendamento.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.core.action.ActionController;

/**
 * Controller para tela de pesquisa por Itens de horários de exames agendados.
 * 
 * @author diego.pacheco
 *
 */

public class PesquisaItemHorarioAgendadoController extends ActionController {

	private static final long serialVersionUID = -755199026260481779L;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	// Parâmetro do page.xml
	private Date dataReativacao;
	
	private List<AelItemSolicitacaoExames> listaItemSolicitacaoExame;
	private List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO;
	private AghAtendimentos atendimento;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		//TODO: DiegoPacheco --> retirar o trecho de código abaixo 
		// qdo a estória #5494 que chama esta estória estiver implementada
		// -- Inicio
		Integer iseSoeSeq = 6104644;
		List<Short> listaSeqp = new ArrayList<Short>();
		listaSeqp.add(Short.valueOf("1"));
		listaSeqp.add(Short.valueOf("2"));
		listaSeqp.add(Short.valueOf("3"));
		listaSeqp.add(Short.valueOf("4"));
		listaSeqp.add(Short.valueOf("5"));
		listaSeqp.add(Short.valueOf("6"));
		
		listaItemSolicitacaoExame = agendamentoExamesFacade.pesquisarItemSolicitacaoExameAtendimentoPacientePorSoeSeqSeqp(iseSoeSeq, listaSeqp);
		// -- Fim	
		
		if (listaItemSolicitacaoExame != null && !listaItemSolicitacaoExame.isEmpty()) {
			atendimento = listaItemSolicitacaoExame.get(0).getSolicitacaoExame().getAtendimento();	
		}
		
		if (atendimento == null) {
			atendimento = new AghAtendimentos();
		}
		
		// TODO: DiegoPacheco --> retirar esse código e descomentar
		// o código mais abaixo que popula listaIseSeqp corretamente
		// qdo a estória #5494 que chama esta estória estiver implementada.
//		List<Short> listaIseSeqp = listaSeqp;
		
//		List<Short> listaIseSeqp = new ArrayList<Short>();
//		for (AelItemSolicitacaoExames itemSolicitacaoExame : listaItemSolicitacaoExame) {
//			listaIseSeqp.add(itemSolicitacaoExame.getId().getSeqp());
//		}
		
		/*listaItemHorarioAgendadoVO = agendamentoExamesFacade
				.pesquisarItemHorarioAgendadoPorIseSoeSeqEListaIseSeqp(iseSoeSeq, listaIseSeqp);*/
	
	}
	
	public void remover(ItemHorarioAgendadoVO itemHorarioVO) {
		listaItemHorarioAgendadoVO.remove(itemHorarioVO);		
	}
	
	/*public String consultarHorario() {
		try {
			agendamentoExamesFacade.verificarItemHorarioAgendadoMarcado(listaItemHorarioAgendadoVO);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			error(e);
			return "erro";
		}
		return "consultarHorario";
	}*/

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public List<ItemHorarioAgendadoVO> getListaItemHorarioAgendadoVO() {
		return listaItemHorarioAgendadoVO;
	}

	public void setListaItemHorarioAgendadoVO(
			List<ItemHorarioAgendadoVO> listaItemHorarioAgendadoVO) {
		this.listaItemHorarioAgendadoVO = listaItemHorarioAgendadoVO;
	}

	public Date getDataReativacao() {
		return dataReativacao;
	}

	public void setDataReativacao(Date dataReativacao) {
		this.dataReativacao = dataReativacao;
	}

	public List<AelItemSolicitacaoExames> getListaItemSolicitacaoExame() {
		return listaItemSolicitacaoExame;
	}

	public void setListaItemSolicitacaoExame(
			List<AelItemSolicitacaoExames> listaItemSolicitacaoExame) {
		this.listaItemSolicitacaoExame = listaItemSolicitacaoExame;
	}
	
}
