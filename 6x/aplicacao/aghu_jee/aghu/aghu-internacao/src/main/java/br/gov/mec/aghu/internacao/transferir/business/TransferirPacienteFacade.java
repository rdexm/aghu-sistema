package br.gov.mec.aghu.internacao.transferir.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.hibernate.Hibernate;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.RapServidores;

/**
 * Porta de entrada do submodulo Transferir Paciente do módulo de internação.
 * 
 * @author lcmoura
 * 
 */

@Modulo(ModuloEnum.INTERNACAO)
@Stateless
public class TransferirPacienteFacade extends BaseFacade implements ITransferirPacienteFacade {

	@EJB
	private TransferirPacienteON transferirPacienteON;

	private static final long serialVersionUID = -6143961227866606542L;

	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<AinLeitos> pesquisarLeitos(Object strPesq, Integer intSeq) {
		return getTransferirPacienteON().pesquisarLeitos(strPesq, intSeq);
	}

	public List<AinLeitos> buscarLeito(String leitoID) {
		return getTransferirPacienteON().buscarLeito(leitoID);
	}

	@Override
	@Secure("#{s:hasPermission('alta','pesquisar')}")
	public Date getDthrAltaMedica(Integer intSeq) {
		return getTransferirPacienteON().getDthrAltaMedica(intSeq);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterInternacao(Integer seq) {
		AinInternacao internacao = getTransferirPacienteON().obterInternacao(seq);
		inicializarAtributosInternacao(internacao);
		return internacao;
	}

	private void inicializarAtributosInternacao(AinInternacao internacao) {
		Hibernate.initialize(internacao.getEspecialidade());
		if (internacao.getEspecialidade() != null) {
			Hibernate.initialize(internacao.getEspecialidade().getClinica());
		}
		Hibernate.initialize(internacao.getServidorProfessor());
		if (internacao.getServidorProfessor() != null) {
			Hibernate.initialize(internacao.getServidorProfessor().getPessoaFisica());
			if (internacao.getServidorProfessor().getPessoaFisica() != null) {
				Hibernate.initialize(internacao.getServidorProfessor().getPessoaFisica().getQualificacoes());
			}
		}
		Hibernate.initialize(internacao.getPaciente());
		Hibernate.initialize(internacao.getLeito());
		if (internacao.getLeito() != null) {
			Hibernate.initialize(internacao.getLeito().getQuarto().getClinica());
		}
		Hibernate.initialize(internacao.getServidorDigita());
		Hibernate.initialize(internacao.getTipoCaracterInternacao());
		Hibernate.initialize(internacao.getOrigemEvento());
		Hibernate.initialize(internacao.getTipoAltaMedica());
		Hibernate.initialize(internacao.getAtendimentoUrgencia());
		Hibernate.initialize(internacao.getInstituicaoHospitalar());
		Hibernate.initialize(internacao.getInstituicaoHospitalarTransferencia());
		Hibernate.initialize(internacao.getItemProcedimentoHospitalar());
		Hibernate.initialize(internacao.getQuarto());
		Hibernate.initialize(internacao.getUnidadesFuncionais());
		Hibernate.initialize(internacao.getConvenioSaude());
		Hibernate.initialize(internacao.getConvenioSaudePlano());
	}

	@Override
	public ProfessorCrmInternacaoVO obterProfessorCrmInternacaoVO(RapServidores servidorProfessor, AghEspecialidades especialidade,
			Short cnvCodigo) {
		return getTransferirPacienteON().obterProfessorCrmInternacaoVO(servidorProfessor, especialidade, cnvCodigo);
	}

	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public void validarQuartoInexistente(String ainQuartosDescricao) throws ApplicationBusinessException {
		getTransferirPacienteON().validarQuartoInexistente(ainQuartosDescricao);
	}

	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public AinQuartos obterQuarto(String ainQuartosDescricao) {
		return getTransferirPacienteON().obterQuartoDescricao(ainQuartosDescricao);
	}

	@Override
	public void validarPacienteJaPossuiAlta(AghAtendimentos atendimento, Date dtTransferencia) throws ApplicationBusinessException {
		getTransferirPacienteON().validarPacienteJaPossuiAlta(atendimento, dtTransferencia);
	}

	@Override
	public void validarDestino(AinInternacao internacao) throws ApplicationBusinessException {
		getTransferirPacienteON().validarDestino(internacao);
	}

	@Override
	public void validaEspecialidade(AinInternacao internacao) throws ApplicationBusinessException {
		getTransferirPacienteON().validaEspecialidade(internacao);
	}

	@Override
	public boolean validarDataTransferencia(Date dataTransf) throws ApplicationBusinessException {
		return getTransferirPacienteON().validarDataTransferencia(dataTransf);
	}

	@Override
	public void validarDataTransferenciaPosteriorAlta(AinInternacao internacao) throws ApplicationBusinessException {
		getTransferirPacienteON().validarDataTransferenciaPosteriorAlta(internacao);
	}

	@Override
	public void validarDataInternacao(AinInternacao internacao) throws ApplicationBusinessException {
		getTransferirPacienteON().validarDataInternacao(internacao);
	}

	@Override
	public boolean consisteClinicaEEspecialidade(AinInternacao internacao) throws ApplicationBusinessException {
		return getTransferirPacienteON().consisteClinicaEEspecialidade(internacao);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','alterar')}")
	public void atualizarInternacao(AinInternacao internacao, AinInternacao oldInternacao, String nomeMicrocomputador) throws BaseException {
		getTransferirPacienteON().atualizarInternacao(internacao, oldInternacao, nomeMicrocomputador);
	}

	/**
	 * Melhoria #8745 Considerar parâmetro de horas para impressão de Controles
	 * na transferência do paciente
	 * 
	 * @param internacaoSeq
	 * @param unidadeSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public Date buscarDataInicioRelatorio(Integer internacaoSeq, Short unidadeSeq) throws ApplicationBusinessException {
		return getTransferirPacienteON().buscarDataInicioRelatorio(internacaoSeq, unidadeSeq);
	}

	@Override
	public boolean deveImprimirControlesPaciente(Short unidadeOrigemSeq, Short unidadeDestinoSeq) {
		return getTransferirPacienteON().deveImprimirControlesPaciente(unidadeOrigemSeq, unidadeDestinoSeq);
	}

	@Override
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public List<ProfessorCrmInternacaoVO> pesquisarProfessor(String strPesq, AghEspecialidades especialidade, AinInternacao internacao) {
		return getTransferirPacienteON().pesquisarProfessor(strPesq, especialidade, internacao);
	}

	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> pesquisarEspecialidades(Object strPesquisa, Integer idadePaciente) {
		return getTransferirPacienteON().pesquisarEspecialidades(strPesquisa, idadePaciente);
	}

	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public List<AghUnidadesFuncionais> pesquisarUnidades(Object strPesquisa) {
		return getTransferirPacienteON().pesquisarUnidades(strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<AinInternacao> pesquisarInternacao(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer prontuario) {
		List<AinInternacao> lista = getTransferirPacienteON().pesquisarInternacao(firstResult, maxResults, orderProperty, asc, prontuario);
		for (AinInternacao internacao : lista) {
			inicializarAtributosInternacao(internacao);
		}
		return lista;
	}

	@Override
	public Long pesquisarInternacaoCount(Integer prontuario) {
		return getTransferirPacienteON().pesquisarInternacaoCount(prontuario);
	}

	protected TransferirPacienteON getTransferirPacienteON() {
		return transferirPacienteON;
	}

	public String atualizarSolicitacaoTransferencia(Integer prontuario, String leitoID, Boolean confirmacaoUsuario)
			throws ApplicationBusinessException {
		return getTransferirPacienteON().atualizarSolicitacaoTransferencia(prontuario, leitoID, confirmacaoUsuario);
	}
	
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public AinLeitos pesquisarApenasLeitoConcedido(Object strPesq, Integer intSeq) {
		return getTransferirPacienteON().pesquisarApenasLeitoConcedido(strPesq, intSeq);
	}

}
