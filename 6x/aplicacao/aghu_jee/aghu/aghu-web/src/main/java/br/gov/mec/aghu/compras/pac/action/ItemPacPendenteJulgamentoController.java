package br.gov.mec.aghu.compras.pac.action;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import br.gov.mec.aghu.dominio.DominioSituacaoJulgamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPacVO;
import br.gov.mec.aghu.core.action.ActionController;

/**
 * Controller responsável por itens do PAC pendentes de julgamento.
 * 
 * @author mlcruz
 */

public class ItemPacPendenteJulgamentoController extends ActionController {

	private static final long serialVersionUID = 8491176996197028006L;

	/** Situações de julgamento de itens pendentes. */
	private static final List<DominioSituacaoJulgamento> SITUACAO_JULGAMENTO_PENDENTE = Arrays.asList(
			DominioSituacaoJulgamento.TE, DominioSituacaoJulgamento.AH, DominioSituacaoJulgamento.EN);

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Indica se um item está pendente.
	 * 
	 * @param item
	 *            Item
	 * @return Flag
	 */
	public boolean isPendente(ScoItemPacVO item) {
		return item != null && Boolean.TRUE.equals(item.getIndJulgada()) && SITUACAO_JULGAMENTO_PENDENTE.contains(item.getItemLicitacaoOriginal().getSituacaoJulgamento());
	}

	/**
	 * Obtem motivo, usuário e data de itens pendentes.
	 * 
	 * @param item
	 *            Item
	 * @return Mensagem
	 */
	public String getPendenteMotivoMessage(ScoItemPacVO item) {
		DominioSituacaoJulgamento situacaoJulgamento = item.getItemLicitacaoOriginal().getSituacaoJulgamento();

		String msg = getBundle().getString("MESSAGE_HINT_PENDENTE_MOTIVO");

		return MessageFormat.format(msg, situacaoJulgamento.getDescricao());
	}

	/**
	 * Obtem motivo, usuário e data de itens pendentes.
	 * 
	 * @param item
	 *            Item
	 * @return Mensagem
	 */
	public String getPendenteUsuarioMessage(ScoItemPacVO item) {
		RapServidores servidor = item.getItemLicitacaoOriginal().getServidorJulgParcial();

		Date dtJulgParcial = item.getItemLicitacaoOriginal().getDtJulgParcial();
		String msg = getBundle().getString("MESSAGE_HINT_PENDENTE_USUARIO");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		return MessageFormat.format(msg, servidor.getPessoaFisica().getNome(), dateFormat.format(dtJulgParcial));
	}
}
