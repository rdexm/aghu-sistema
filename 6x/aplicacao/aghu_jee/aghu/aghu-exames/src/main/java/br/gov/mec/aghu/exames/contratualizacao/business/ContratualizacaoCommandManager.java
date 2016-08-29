package br.gov.mec.aghu.exames.contratualizacao.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ContratualizacaoCommandManager {
	
	@Inject
	private PacienteContratualizacaoCommand pacienteContratualizacaoCommand;
	
	@Inject
	private MedicoExternoContratualizacaoCommand medicoExternoContratualizacaoCommand;

	@Inject
	private AtendimentoPacExternoContratualizacaoCommand atendimentoPacExternoContratualizacaoCommand;

	@Inject
	private ItemSolicitacaoExamesContratualizacaoCommand itemSolicitacaoExamesContratualizacaoCommand;

	@Inject
	private SolicitacaoContratualizacaoCommand solicitacaoContratualizacaoCommand;
	
	public List<ContratualizacaoCommand> getActions() {
		List<ContratualizacaoCommand> acoes = new ArrayList<ContratualizacaoCommand>();

		acoes.add(pacienteContratualizacaoCommand);
		acoes.add(medicoExternoContratualizacaoCommand);
		acoes.add(atendimentoPacExternoContratualizacaoCommand);
		acoes.add(itemSolicitacaoExamesContratualizacaoCommand);		
		acoes.add(solicitacaoContratualizacaoCommand);
		
		return acoes;
	}
	
}
