/**
 * 
 */
package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.Date;
import java.util.Iterator;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoQualificacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author aghu
 *
 */
@Stateless
public class SumarioAtendimentoRecemNascidoRN extends BaseBusiness {


@EJB
private RelatorioAtendEmergObstetricaRN relatorioAtendEmergObstetricaRN;

private static final Log LOG = LogFactory.getLog(SumarioAtendimentoRecemNascidoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IAghuFacade aghuFacade;


	private static final long serialVersionUID = -5840728500711275589L;

	/**
	 * Acesso ao modulo 
	 * 
	 * @return
	 */
	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	
	/**
	 * @ORADB - MCOC_VER_CONV
	 * Dado um paciente e uma consulta retorna o convenio
	 * @param pPacCodigo - codigo do paciente
	 * @param pConNumero - numero da consulta
	 * @return
	 * @author daniel.silva
	 * @since 07/08/2012
	 */
	public String obterDescricaoConvenio(Integer pPacCodigo, Integer pConNumero) {
		String vRetorno = null;
		
		if (pConNumero != null) {
			Boolean vAchouReg = Boolean.TRUE;
			AghAtendimentos atendimento = getAghuFacade().obterAtendimentoPorCodigoPacienteNumeroConsulta(pPacCodigo, pConNumero);
			if (atendimento == null) {
				vAchouReg = Boolean.FALSE;
			}
			if (vAchouReg) {
				if (atendimento.getInternacao() != null) {
					//busca convenio na internação
					FatConvenioSaude cnv = atendimento.getInternacao().getConvenioSaude();
					if (cnv != null) {
						vRetorno = cnv.getDescricao();
					}
				} else if (atendimento.getAtendimentoUrgencia() != null) {
		            //busca o convenio no atendimento de urgencia
					FatConvenioSaude cnv = atendimento.getAtendimentoUrgencia().getConvenioSaude();
					if (cnv != null) {
						vRetorno = cnv.getDescricao();
					}
				} else {
		        	//busca o convenio na consulta
					FatConvenioSaudePlano csp = atendimento.getConsulta().getConvenioSaudePlano();
					if (csp != null && csp.getConvenioSaude() != null) {
						vRetorno = csp.getConvenioSaude().getDescricao();
					}
				}
			}
		}
		return vRetorno;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	
	
	
	/**
	 * @ORADB RAPC_BUSCA_NOME
	 * 
	 * Obtem o nome do profissional envolvido no nascimento
	 * 
	 * @param matricula
	 * @param vinCodigo
	 * 
	 * @author guilherme.finotti
	 * @since 09/08/2012
	 */
	public String obterNomeProfissional(Integer matricula, Short vinCodigo) {
		RapServidores servidor = getRegistroColaboradorFacade().buscarServidor(vinCodigo, matricula);
		if(servidor != null && servidor.getPessoaFisica() != null) {
			return servidor.getPessoaFisica().getNome();
		}
		return "";
	}
	
	
	
	/**
	 * @ORADB RAPC_BUS_NOM_NR_CONS
	 * 
	 * fixed rubens.silva
	 * 
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 * @author bruno.mourao
	 * @since 08/08/2012
	 */
	public String obterNomeNumeroConselho(Integer matricula, Short vinCodigo){
		StringBuilder retorno = new StringBuilder();
		
		RapServidores servidor = getRegistroColaboradorFacade().buscarServidor(vinCodigo, matricula);
		
		if(servidor != null && (servidor.getDtFimVinculo() == null || servidor.getDtFimVinculo().after(new Date())) && servidor.getPessoaFisica() != null){
			String nroRegConselho = null;
			RapQualificacao qualificacao = null;
		
			//Obtem nroRegConselho
			if(servidor.getPessoaFisica().getQualificacoes() != null && !servidor.getPessoaFisica().getQualificacoes().isEmpty()){
				Iterator<RapQualificacao> iterator = servidor.getPessoaFisica().getQualificacoes().iterator();
				while (iterator.hasNext()) {
					RapQualificacao qlf = iterator.next();
					if(qlf.getSituacao().equals(DominioSituacaoQualificacao.C) && qlf.getTipoQualificacao() != null && qlf.getTipoQualificacao().getConselhoProfissional() != null){
						qualificacao = qlf;
						nroRegConselho = qlf.getNroRegConselho();
						break;
					}					
				}
			}
			
			//Obtem sigla
			if(nroRegConselho != null){
				//Monta o resultado
				retorno.append(qualificacao.getTipoQualificacao().getConselhoProfissional().getSigla());
				retorno.append(' ');
				retorno.append(nroRegConselho);
			}
			
		}
		
		return retorno.toString();
	}
	
	/**
	 * @ORADB BeforeReport
	 * 
	 * @see retorna o cpResponsavel de acordo a matricula e vinculo informado
	 * 
	 * @param matricula
	 * @param vinculo
	 * @return CPResponsavel
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public String obterCPResponsavel(Integer matricula, Short vinculo) throws ApplicationBusinessException {
		
		BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO = getRelatorioAtendEmergObstetricaRN().buscaConselhoProfissionalServidorVO(matricula, vinculo);

		String cpResponsavel = "";
		if(buscaConselhoProfissionalServidorVO != null &&
				buscaConselhoProfissionalServidorVO.getNome() != null &&
				  buscaConselhoProfissionalServidorVO.getSiglaConselho() != null &&
				  buscaConselhoProfissionalServidorVO.getNumeroRegistroConselho() != null) {
		
			cpResponsavel =  buscaConselhoProfissionalServidorVO.getNome().concat("   ").
								concat(buscaConselhoProfissionalServidorVO.getSiglaConselho()).concat("   ").
								concat(buscaConselhoProfissionalServidorVO.getNumeroRegistroConselho());
		}
		
		if(StringUtils.isBlank(cpResponsavel)) {
			cpResponsavel = obterNomeProfissional(matricula, vinculo);
		}
		
		
		return cpResponsavel;
	}

	private RelatorioAtendEmergObstetricaRN getRelatorioAtendEmergObstetricaRN() {
		return relatorioAtendEmergObstetricaRN;
	}


	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
}
