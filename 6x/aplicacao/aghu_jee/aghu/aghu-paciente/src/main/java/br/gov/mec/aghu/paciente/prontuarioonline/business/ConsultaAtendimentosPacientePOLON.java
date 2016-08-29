package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ConsultaAtendimentosPacientePOLON extends BaseBusiness
		implements Serializable {

private static final Log LOG = LogFactory.getLog(ConsultaAtendimentosPacientePOLON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7773398290050904762L;

	public List<AtendimentosVO> pesquisarAtendimentosPorProntuario (Integer numeroProntuario) throws ApplicationBusinessException{
		
		Set<AtendimentosVO> atendimentos = new TreeSet<>(new Comparator<AtendimentosVO>(){
		    public int compare(AtendimentosVO b, AtendimentosVO a){
		        return a.getDataAtendimento().compareTo(b.getDataAtendimento());
		    }
		});		
		/**
		 * concatencao das Listas
		 */
		atendimentos.addAll(pesquisarAtendimentosInternacao(numeroProntuario));
		atendimentos.addAll(pesquisarAtendimentosEmergencia(numeroProntuario));
		atendimentos.addAll(pesquisarAtendimentosSO(numeroProntuario));
		atendimentos.addAll(pesquisarAtendimentosConsultas(numeroProntuario));
		atendimentos.addAll(pesquisarAtendimentosCirurgias(numeroProntuario));
		atendimentos.addAll(pesquisarAtendimentosNascimentos(numeroProntuario));
		atendimentos.addAll(pesquisarAtendimentosNeonatologia(numeroProntuario));
		//TODO descomentar apos criancao do Parametro P_PAC_EXTERNO_POL
		// Verifica se vai mostrar pacientes externos na consulta final		
		AghParametros pacExterno = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PAC_EXTERNO_POL);
		if (pacExterno.getVlrNumerico().equals(1)) {
			atendimentos.addAll(pesquisarAtendimentosPacExternos(numeroProntuario));
		}
		
		return new ArrayList<AtendimentosVO>(atendimentos);
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	public List<AtendimentosVO> pesquisarAtendimentosInternacao(
			Integer numeroProntuario) throws ApplicationBusinessException {
		/**
		 *  Tipo - Internacao
		 */
		AghParametros origem = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_ORIGEM_EMERGENCIA);
		
		
		Short vlrOrigem = origem.getVlrNumerico().shortValueExact();
		
		List<AtendimentosVO> listaAtendimentosInternacao = 
			getAghuFacade().pesquisarAtendimentosInternacao(
					numeroProntuario, new Date(), vlrOrigem);
				
		for(AtendimentosVO vo : listaAtendimentosInternacao){
			
			// RESTRICAO VINDA DA FUNCAO RAPC_BUSCA_NOME
			String nomeServidor;
			if(vo.getMatriculaServidor() != null){
				RapServidores serv = getRegistroColaboradorFacade().
				obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula
				(vo.getMatriculaServidor(),vo.getVinCodigoServidor());
				RapPessoasFisicas pessoa = serv.getPessoaFisica();
				if(pessoa != null){
					nomeServidor = pessoa.getNome();
				}else{
					nomeServidor = "";
				}
			}else {
				nomeServidor = "";
			}
			
			vo.setDataAtendimento(vo.getDataAtendimento());
			vo.setTipoAtendimento("INTERNACAO");
			vo.setEspecialidadeServico(vo.getNomeReduzidoEspecialidade() + "," + vo.getDescricaoCentroCusto());
			vo.setProfissional(nomeServidor);
		}
	
		
		return listaAtendimentosInternacao;
	}

	private IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public List<AtendimentosVO> pesquisarAtendimentosEmergencia (
			Integer numeroProntuario) throws ApplicationBusinessException {
		
		/**
		 *  Tipo - Internacao - Emergencia
		 */	
		AghParametros origem = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_ORIGEM_EMERGENCIA);
		
		Short vlrOrigem = origem.getVlrNumerico().shortValueExact();
		
		List<AtendimentosVO> listaAtendimentosEmergencia = 
			getAghuFacade().pesquisarAtendimentosInternacaoEmergencia
			(numeroProntuario, new Date(), vlrOrigem);
		
		for(AtendimentosVO vo : listaAtendimentosEmergencia){
			
			// RESTRICAO VINDA DA FUNCAO RAPC_BUSCA_NOME
			String nomeServidor;
			if(vo.getMatriculaServidor() != null){
				RapServidores serv = getRegistroColaboradorFacade().
				obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula
				(vo.getMatriculaServidor(),vo.getVinCodigoServidor());
				RapPessoasFisicas pessoa = serv.getPessoaFisica();
				if(pessoa != null){
					nomeServidor = pessoa.getNome();
				}else{
					nomeServidor = "";
				}
			} else {
				nomeServidor = "";
			}
			
			vo.setDataAtendimento(vo.getDataAtendimento());
			vo.setTipoAtendimento("INTERNACAO");
			vo.setEspecialidadeServico("ORIGINADO NA EMERGENCIA: " + vo.getNomeReduzidoEspecialidade() + "," + vo.getDescricaoCentroCusto());
			vo.setProfissional(nomeServidor);
		}
		
		return listaAtendimentosEmergencia;
	}
	
	public List<AtendimentosVO> pesquisarAtendimentosSO (Integer numeroProntuario) {
		
		/**
		 *  Tipo - SO
		 */	
		
		List<AtendimentosVO> listaAtendimentosSO = 
			getAghuFacade().pesquisarAtendimentosSO(numeroProntuario, new Date());
		
		for(AtendimentosVO vo : listaAtendimentosSO){
			
			/**
			 * Para esse tipo de atendimento (SO), o nome do servidor pode vir de atributos (Vinculo e Matricula)
			 * diferentes.
			 * matriculaServidor e vinCodigoServidor vem da tabela AghEquipes
			 * matriculaServidor2 e vinCodigoServidor2 vem da tabela AacGradeAgendamenConsultas
			 */
			// RESTRICAO VINDA DA FUNCAO RAPC_BUSCA_NOME
			String nomeServidor;
			if(vo.getMatriculaServidor() != null){
				RapServidores serv = getRegistroColaboradorFacade().
				obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula
				(vo.getMatriculaServidor(),vo.getVinCodigoServidor());
				RapPessoasFisicas pessoa = serv.getPessoaFisica();
				if(pessoa != null){
					nomeServidor = pessoa.getNome();
				}else{
					nomeServidor = "";
				}
			}else {
				if(vo.getMatriculaServidor2() != null){
					RapServidores serv = getRegistroColaboradorFacade().
					obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula
					(vo.getMatriculaServidor2(),vo.getVinCodigoServidor2());
					RapPessoasFisicas pessoa = serv.getPessoaFisica();
					if(pessoa != null){
						nomeServidor = pessoa.getNome();
					}else{
						nomeServidor = "";
					}
				}else {
					nomeServidor = "";
				}
			}
 
			
			vo.setDataAtendimento(vo.getDataAtendimento());
			vo.setTipoAtendimento("SO");
			vo.setEspecialidadeServico(vo.getNomeReduzidoEspecialidade() + "," + vo.getDescricaoCentroCusto()); 
			vo.setProfissional(nomeServidor);
		}
		return listaAtendimentosSO;
	}
	
	public List<AtendimentosVO> pesquisarAtendimentosConsultas (Integer numeroProntuario) {
		
		/**
		 *  Tipo - CONSULTAS
		 */	
		
		List<AtendimentosVO> listaAtendimentosConsultas = 
			getAghuFacade().pesquisarAtendimentosConsultas(numeroProntuario, new Date());
		
		for(AtendimentosVO vo : listaAtendimentosConsultas){
			
			/**
			 * Para esse tipo de atendimento (Consultas), o nome do servidor pode vir de atributos (Vinculo e Matricula)
			 * diferentes.
			 * matriculaServidor e vinCodigoServidor vem da tabela AghEquipes
			 * matriculaServidor2 e vinCodigoServidor2 vem da tabela AacGradeAgendamenConsultas
			 */
			// RESTRICAO VINDA DA FUNCAO RAPC_BUSCA_NOME
			String nomeServidor;
			if(vo.getMatriculaServidor() != null){
				RapServidores serv = getRegistroColaboradorFacade().
				obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula
				(vo.getMatriculaServidor(),vo.getVinCodigoServidor());
				RapPessoasFisicas pessoa = serv.getPessoaFisica();
				if(pessoa != null){
					nomeServidor = pessoa.getNome();
				}else{
					nomeServidor = "";
				}
			}else {
				if(vo.getMatriculaServidor2() != null){
					RapServidores serv = getRegistroColaboradorFacade().
					obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula
					(vo.getMatriculaServidor2(),vo.getVinCodigoServidor2());
					RapPessoasFisicas pessoa = serv.getPessoaFisica();
					if(pessoa != null){
						nomeServidor = pessoa.getNome();
					}else{
						nomeServidor = "";
					}
				}else {
					nomeServidor = "";
				}
			}
 
			
			vo.setDataAtendimento(vo.getDataAtendimento());
			vo.setTipoAtendimento("CONSULTA");
			vo.setEspecialidadeServico(vo.getNomeReduzidoEspecialidade() + "," + vo.getDescricaoCentroCusto()); 
			vo.setProfissional(nomeServidor);
		}
		return listaAtendimentosConsultas;
	}

	public List<AtendimentosVO> pesquisarAtendimentosCirurgias (Integer numeroProntuario) {
		
		/**
		 *  Tipo - CIRURGIAS
		 */	
		
		List<AtendimentosVO> listaAtendimentosCirurgias = 
			getAghuFacade().pesquisarAtendimentosCirurgias(numeroProntuario, new Date());
		
		for(AtendimentosVO vo : listaAtendimentosCirurgias){
			
			// RESTRICAO VINDA DA FUNCAO RAPC_BUSCA_NOME
			String nomeServidor;
			if(vo.getMatriculaServidor() != null){
				RapServidores serv = getRegistroColaboradorFacade().
				obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(
						vo.getMatriculaServidor(),vo.getVinCodigoServidor());
				RapPessoasFisicas pessoa = serv.getPessoaFisica();
				if(pessoa != null){
					nomeServidor = pessoa.getNome();
				}else{
					nomeServidor = "";
				}
			}else {
				nomeServidor = "";
			}
 
			
			vo.setDataAtendimento(vo.getDataAtendimento());
			vo.setTipoAtendimento("CIRURGIA");
			vo.setEspecialidadeServico(vo.getNomeReduzidoEspecialidade() + "," + vo.getDescricaoCentroCusto()); 
			vo.setProfissional(nomeServidor);
		}
		return listaAtendimentosCirurgias;
	}
	
	public List<AtendimentosVO> pesquisarAtendimentosPacExternos (Integer numeroProntuario) throws ApplicationBusinessException {
		
		/**
		 *  Tipo - PAC-EXTERNOS
		 */	
		
		List<AtendimentosVO> listaAtendimentosPacExterno = 
			getAghuFacade().pesquisarAtendimentosPacExterno(numeroProntuario, new Date());
		
		for(AtendimentosVO vo : listaAtendimentosPacExterno){
			
			vo.setDataAtendimento(vo.getDataAtendimento());
			vo.setTipoAtendimento("PAC EXTERNO");
			vo.setEspecialidadeServico("REALIZACAO DE EXAMES: " + vo.getNomeReduzidoEspecialidade() + "," + vo.getDescricaoCentroCusto());
		}
		return listaAtendimentosPacExterno;
	}
	
	public List<AtendimentosVO> pesquisarAtendimentosNascimentos (Integer numeroProntuario) {
	/**
	 *  Tipo - NASCIMENTOS
	 */	
	
	List<AtendimentosVO> listaAtendimentosNascimentos = 
		getAghuFacade().pesquisarAtendimentosNascimentos(numeroProntuario, new Date());
	
	for(AtendimentosVO vo : listaAtendimentosNascimentos){
		
		vo.setDataAtendimento(vo.getDataAtendimento());
		vo.setTipoAtendimento("NASCIMENTO");
		vo.setEspecialidadeServico("EQUIPE OBSTETRICIA: " + vo.getNomeDescricaoEquipe());
	}
	return listaAtendimentosNascimentos;
	}

	public List<AtendimentosVO> pesquisarAtendimentosNeonatologia (Integer numeroProntuario) {
		/**
		 *  Tipo - NEONATOLOGIA
		 */	
		
		List<AtendimentosVO> listaAtendimentosNeonatologia = 
			getAghuFacade().pesquisarAtendimentosNeonatologia(numeroProntuario, new Date());
		
		for(AtendimentosVO vo : listaAtendimentosNeonatologia){
			
			// RESTRICAO VINDA DA FUNCAO RAPC_BUSCA_NOME
			String nomeServidor;
			if(vo.getMatriculaServidor() != null){
				RapServidores serv = getRegistroColaboradorFacade().
				obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(
						vo.getMatriculaServidor(),vo.getVinCodigoServidor());
				RapPessoasFisicas pessoa = serv.getPessoaFisica();
				if(pessoa != null){
					nomeServidor = pessoa.getNome();
				}else{
					nomeServidor = "";
				}
			}else {
				nomeServidor = "";
			}
			
			vo.setDataAtendimento(vo.getDataAtendimento());
			vo.setTipoAtendimento("NASCIMENTO");
			vo.setEspecialidadeServico("EQUIPE NEONATOLOGIA: ");
			vo.setProfissional(nomeServidor);
		}
		return listaAtendimentosNeonatologia;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
