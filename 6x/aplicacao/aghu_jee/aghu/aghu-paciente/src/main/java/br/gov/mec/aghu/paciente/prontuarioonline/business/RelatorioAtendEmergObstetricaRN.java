/**
 * 
 */
package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * @author aghu
 *
 */
@Stateless
public class RelatorioAtendEmergObstetricaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioAtendEmergObstetricaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private static final long serialVersionUID = 2101511870624588961L;
	
	private static final Integer TAMANHO_MAXIMO_NOME = 40;
	
	/**
	 * @ORADB CSEP_BUSCA_CONS_PROF
	 *  
	 * Utilizar MCOC_BUSCA_CONS_PROF
	 * Ler #24569 #22389 
	 * 
	 * Importante: Não será considerada a AÇÃO neste método! A validação de
	 * permissões deve ser feito utilizando os mecanismos criados no AGHU para
	 * validação de permissões.
	 * 
	 * Busca informações do conselho profissional do servidor necessário para
	 * executar uma ação. Caso os parâmetros p_matricula e p_vinculo venham
	 * preenchidos, será considerado o servidor deste vinculo e matricula
	 * informados. Caso tais parâmetros não sejam informados, será considerado o
	 * usuário conectado ao sistema e serão preenchidos (para retornar também)
	 * os parâmetros matricula e vinculo. Será procurado e retornado a primeira
	 * qualificação do servidor associada à ação em ordem alfabética de sigla de
	 * conselho. Por exemplo, se usuário for Médico (CRM) e Dentista (CRO), será
	 * retornado o CRM e seu número.
	 * 
	 * @param matricula
	 * @param vinculo
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 * @deprecated 
	 */
	public BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO(Integer matricula, Short vinculo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (matricula == null || vinculo == null) {
			RapServidores ser = servidorLogado;
			if (ser != null) {
				matricula = ser.getId().getMatricula();
				vinculo = ser.getId().getVinCodigo();				
			}
		}
		
		if (matricula == null || vinculo == null) {
			return null;
		}

		List<ConselhoProfissionalServidorVO> conselhos = getRegistroColaboradorFacade().buscaConselhosProfissionalServidor(matricula, vinculo, Boolean.FALSE);
		
		if (conselhos == null || conselhos.isEmpty()) {
			return null;
		} else {
			ConselhoProfissionalServidorVO conselhoVO = conselhos.iterator().next();
			return obterBuscaConselhoProfissionalServidorVO(matricula, vinculo, conselhoVO);
		}
	}
	
	private BuscaConselhoProfissionalServidorVO obterBuscaConselhoProfissionalServidorVO(Integer matricula, Short vinculo, ConselhoProfissionalServidorVO conselhoVO) {
		BuscaConselhoProfissionalServidorVO vo = new BuscaConselhoProfissionalServidorVO();		
		vo.setMatricula(matricula);
		vo.setVinculo(vinculo);
		vo.setNome(conselhoVO.getNome());
		vo.setCpf(CoreUtil.formataCPF(conselhoVO.getCpf()));
		vo.setSiglaConselho(conselhoVO.getSiglaConselho());
		vo.setNumeroRegistroConselho(conselhoVO.getNumeroRegistroConselho());
		vo.setNome(obterNome(conselhoVO));			
		return vo;
	}
	
	private String obterNome(ConselhoProfissionalServidorVO vo) {
		StringBuilder nome = new StringBuilder();
		if (vo.getNumeroRegistroConselho() != null && vo.getSexo() != null) {
			if (DominioSexo.M.equals(vo.getSexo()) && vo.getTituloMasculino() != null) {
				nome.append(vo.getTituloMasculino()).append(' ');
			} else if (DominioSexo.F.equals(vo.getSexo()) && vo.getTituloFeminino() != null) {
				nome.append(vo.getTituloFeminino()).append(' ');
			}
			nome.append(obterNomeComTamanhoMaximo(vo.getNome()));
		}	
		return nome.toString();
	}
	
	private String obterNomeComTamanhoMaximo(String nome) {
		return nome.length() <= TAMANHO_MAXIMO_NOME ? nome : nome.substring(0, TAMANHO_MAXIMO_NOME);
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
