package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioEventoNotaAdicional;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO.ParametrosReportEnum;
import br.gov.mec.aghu.model.McoNotaAdicional;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class SumarioAdmissaoObstetricaNotasAdRN extends BaseBusiness {


@EJB
private RelExameFisicoRecemNascidoPOLON relExameFisicoRecemNascidoPOLON;

private static final Log LOG = LogFactory.getLog(SumarioAdmissaoObstetricaNotasAdRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IPerinatologiaFacade perinatologiaFacade;

	private static final long serialVersionUID = -7151319014103328380L;

	protected IPerinatologiaFacade getPerinatologiaFacade(){
		return perinatologiaFacade;
	}
	
	/**
	 * Q_1
	 * @param vo
	 * return NAD_NOTA_ADICIONAL
	 * return NAD_CRIADO_EM
	 * return NAD_NOME_PROF
	 * @throws ApplicationBusinessException  
	 */
	public void executarQ1(SumarioAdmissaoObstetricaInternacaoVO vo) throws ApplicationBusinessException {
        
        Integer pacCodigo = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.P_PAC_CODIGO);
        Short gsoSeqp = (Short) vo.getParametrosHQL().get(ParametrosReportEnum.P_GSO_SEQP);
        Integer conNumero = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.P_CON_NUMERO);
        Integer matricula = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.P_MATRICULA);
        Short vinCodigo = (Short) vo.getParametrosHQL().get(ParametrosReportEnum.P_VIN_CODIGO);
        
        if(pacCodigo != null && gsoSeqp != null && conNumero != null){
                List<McoNotaAdicional> notasAdicionais = getPerinatologiaFacade().pesquisarNotaAdicionalPorPacienteGestacaoConsulta(pacCodigo, gsoSeqp, conNumero);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, hh:mm");
                if(!notasAdicionais.isEmpty()){        
                        for(McoNotaAdicional notaAdicional: notasAdicionais){
                                if(notaAdicional.getEvento().equals(DominioEventoNotaAdicional.MCOR_ADMISSAO_OBS)){
                                        //NAD_NOTA_ADICIONAL
                                        vo.setNotaAdicional(notaAdicional.getNotaAdicional());
                                        //NAD_CRIADO_EM
                                        if(notaAdicional.getCriadoEm() != null){
                                                StringBuilder criadoEm = new StringBuilder();
                                                criadoEm.append(sdf.format(notaAdicional.getCriadoEm())).append('h');
                                                vo.setDataNota(criadoEm.toString());
                                        }
                                        vo.setNomeRespNota(getRelExameFisicoRecemNascidoPOLON().formataNomeProf(matricula, vinCodigo));
                                        break;
                                }
                        }
                }
        }
        
	}
	
	private RelExameFisicoRecemNascidoPOLON getRelExameFisicoRecemNascidoPOLON() {
		return relExameFisicoRecemNascidoPOLON;
	}
	
	/**
	 * @ORADB - MCOC_BUSCA_CONS_PROF
	 * Retorna lista de qualificacoes do profissional
	 * 
	 * Esta função original utilizava tabelas CSE (cse_acoes, cse_acoes_con_profissionais, etc)
	 * Foi migrada para utilizar o CASCA, ou seja, na função original é feito:
	 * 		aco.descricao 		= 	'VALIDAR PRESCRICAO MEDICA'
	 * agora é feito atráves de:
	 * 		getCascaFacade().usuarioTemPermissao(servidor.getUsuario(), "confirmarPrescricaoMedica");
	 * Importante lembrar que permissão que está sendo verificada é do usuário que está sendo passado como parametro.
	 * Então se é passado a matricula e vinCodigo de um Médico, é verificado se 
	 * este médido tem a permissão "confirmarPrescricaoMedica" (o médico tem que ter cadastro, login, etc) 
	 * e não se o usuário logado é que possui esta permissão
	 * 
	 * 
	 * TODO: Verificar a informação acima pois a mesma não funcionará para medicos que 
	 * deixem o hospital e assim parem de possuir usuario no sistema impedindo que tenham
	 * permissoes e retornando de forma errada que o referido medico nao possua CRM por exemplo.
	 * Verificando o SQL original, notei que a permissao citada eh utilizada para saber quais conselhos
	 * tem ela associada e nao qual usuario tem tal permissao, sendo assim foi comentada essa validação.
	 *
	 *
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	@SuppressWarnings("deprecation") 
	public List<RapQualificacao> buscarQualificacoesDoProfissional(Integer matricula, Short vinCodigo) throws ApplicationBusinessException {
	
		if(matricula == null || vinCodigo == null) {
			return null;
		}
		
		List<RapQualificacao> qualificacoes = new ArrayList<RapQualificacao>();
		RapServidores servidor = getRegistroColaboradorFacade().buscarServidor(vinCodigo, matricula);
		//comentado conforme a citação acima.
		//Boolean servidorPodeValidarPrescricaoMedica = false;
		
		if (servidor != null) {
			if(servidor.getUsuario() == null){
				//usuario sem login na matricula atual (matricula desativada)
				//busca o usuario do servidor a partir do codigo da pessoa fisica
				RapPessoasFisicas pf = servidor.getPessoaFisica();
				List<RapServidores> servidoresPF = getRegistroColaboradorFacade().pesquisarRapServidoresPorCodigoPessoa(pf.getCodigo());
				for (RapServidores servidorPF : servidoresPF) {
					if(servidorPF.getUsuario() != null){
						servidor = servidorPF;
						break;
					}
				}
			}
			
			/*if(servidor.getUsuario() != null){
				//Ver comentário do método
				servidorPodeValidarPrescricaoMedica = getCascaFacade().usuarioTemPermissao(servidor.getUsuario(), "confirmarPrescricaoMedica");
			}*/
		}

		// Se não pode 'VALIDAR PRESCRICAO MEDICA' (conforme estava na 
		// tabela CSE_ACOES), não faz nada
		if (/*servidorPodeValidarPrescricaoMedica && */servidor.getPessoaFisica() != null 
				&& servidor.getPessoaFisica().getQualificacoes() != null 
				&& !servidor.getPessoaFisica().getQualificacoes().isEmpty()) {			
			for(RapQualificacao qualificacao : servidor.getPessoaFisica().getQualificacoes()) {
				// conselho ind situacao = 'A'tivo e nro conselho is not null 
				if(qualificacao.getTipoQualificacao().getConselhoProfissional() != null 
						&& DominioSituacao.A.equals(qualificacao.getTipoQualificacao().getConselhoProfissional().getIndSituacao())
						&& StringUtils.isNotBlank(qualificacao.getNroRegConselho())) {
					qualificacoes.add(qualificacao);
				}
			}
		}	
		return qualificacoes;
	}
	
	/**
	 * Q1.NAD_NOME_PROF
	 * @param matricula
	 * @param vinCodigo
	 * @param qualificacoes
	 * @return
	 */
	public String obterNomeProfissional(Integer matricula, Short vinCodigo, List<RapQualificacao> qualificacoes) {
		
		StringBuilder retorno = new StringBuilder();
		RapServidores servidor = getRegistroColaboradorFacade().buscarServidor(vinCodigo, matricula);
		
		if(servidor != null && servidor.getPessoaFisica() != null) {

			String nome = servidor.getPessoaFisica().getNome();
			DominioSexo sexo = servidor.getPessoaFisica().getSexo();
			
			String tituloMasculino = "";
			String tituloFeminino = "";			
			String nroRegConselho = "";
			String sigla = "";			
			RapQualificacao qualificacao = null;
		
			// caso tenha mais de uma qualificacao, pega a primeira ordenada pela sigla do conselho
			for(RapQualificacao obj : qualificacoes) {
				String siglaAtual = obj.getTipoQualificacao().getConselhoProfissional().getSigla();
				if(sigla.compareToIgnoreCase(siglaAtual) < 0) {
					sigla = obj.getTipoQualificacao().getConselhoProfissional().getSigla();
					qualificacao = obj;
				}
			}				
			if(qualificacao != null) {
				nroRegConselho = qualificacao.getNroRegConselho();
				sigla = qualificacao.getTipoQualificacao().getConselhoProfissional().getSigla();					
				tituloMasculino = qualificacao.getTipoQualificacao().getConselhoProfissional().getTituloMasculino();
				tituloFeminino = qualificacao.getTipoQualificacao().getConselhoProfissional().getTituloFeminino();
			}
			// monta o retorno
			if(DominioSexo.M.equals(sexo)) {
				retorno.append(tituloMasculino);
			} else {
				retorno.append(tituloFeminino);
			}
			retorno.append(' ');
			retorno.append(nome);
			retorno.append("    ");
			retorno.append(sigla);
			retorno.append("    ");
			retorno.append(nroRegConselho);
		}		
		return retorno.toString();
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
