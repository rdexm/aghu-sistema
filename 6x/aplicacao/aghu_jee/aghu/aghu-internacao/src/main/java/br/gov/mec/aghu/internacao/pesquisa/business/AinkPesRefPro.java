package br.gov.mec.aghu.internacao.pesquisa.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ContaAptosNewVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ContaCtiNewVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ContaInternadosNewVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ContaOutrasClinicasNewVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ContaOutrasUnidadesNewVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;

/**
 * 
 * Classe de apoio para as Business Facades. Ela em geral agrupa as
 * funcionalidades encontradas em packages e procedures do AGHU.
 * 
 * Ela poderia ser uma classe com acesso friendly ou default e não ser um
 * componente seam.
 * 
 * Mas fazendo assim facilita, pois ela também pode receber uma referência para
 * o EntityManager.
 * 
 * Outra forma de fazer é instanciar ela diretamente do ON e passar o entity
 * manager para seus parâmetros. Neste caso os metodos desta classe poderiam ser
 * até estaticos e nao necessitar de instanciação. Ai ela seria apenas um
 * particionamento lógico de código e não um componente que possa ser injetado
 * em qualquer outro contexto.
 * 
 * ATENÇÃO, Os metodos desta classe nunca devem ser acessados diretamente por
 * qualquer classe que não a ON correspondente. Por isso sugere-se que todos os
 * métodos desta sejam friendly (default) ou private.
 * 
 */
@Stateless
public class AinkPesRefPro extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AinkPesRefPro.class);
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	
	@EJB
	private PesquisaInternacaoRN pesquisaInternacaoRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3292494171687296794L;
	// FIXME 157 = Magic Number migrado do AGH, referente a internações SIDA
	// ( HOSPITAL DIA SIDA - AIDS ). Refatorar.
	static final Short HOSPITAL_DIA_SIDA_AIDS = 157; 
	
	/* Conta internações por profissional para leitos do referencial */
	public ContaInternadosNewVO contaInternadosNew(Short vinculo, Integer matricula, Short especialidade) {
		// --------------------------------------------------------------------------------------------------------------
		List<Object[]> resultList = getAinInternacaoDAO().contaInternadosNew(vinculo, matricula, especialidade);
		// --------------------------------------------------------------------------------------------------------------

		Integer pac = resultList.size();
		Integer ast = 0;
		Integer adm = 0;

		for (Object[] item : resultList) {
			Integer codigo = (Integer) item[2]; // projecaoTciSeq
			if (codigo.intValue() == 2) {
				adm++;
			} else {
				ast++;
			}
		}

		return new ContaInternadosNewVO(pac, ast, adm);
	}
	
	/*
	 * Conta leitos bloqueados em função do paciente do quarto considerando o
	 * profissional do mesmo
	 */
	public Long contaBloqueios(Short vinculo, Integer matricula, Short especialidade) {
		return getAinInternacaoDAO().contaBloqueios(vinculo, matricula, especialidade);
	}
	
	/* Conta internações em CTI considerando também os pacientes extras(sem leito)*/
	public ContaCtiNewVO contaCtiNew(Short vinculo, Integer matricula, Short especialidade) {
		Integer cti = null;
		Integer adm = 0;
		Integer ast = 0;
		
		List<AinInternacao> listaInternacoes = getAinInternacaoDAO().pesquisarContaCti(vinculo, matricula, especialidade);
		
		for (int i = listaInternacoes.size() - 1; i >= 0 ; i--) {
			AinInternacao ainInternacao = listaInternacoes.get(i);
			AghUnidadesFuncionais unidadesFuncionais = ainInternacao.getUnidadesFuncionais();
			AinQuartos quarto = ainInternacao.getQuarto();
			AinLeitos leito = ainInternacao.getLeito();
			
			Short pUnfSeq = unidadesFuncionais == null ? null : unidadesFuncionais.getSeq(); 
			Short pQuarto = quarto == null ? null : quarto.getNumero(); 
			String pLeito = leito == null ? null : leito.getLeitoID();
			
			pUnfSeq = this.getPesquisaInternacaoRN().aincRetUnfSeq(pUnfSeq, pQuarto, pLeito);
			ConstanteAghCaractUnidFuncionais caracteristica = ConstanteAghCaractUnidFuncionais.LAUDO_CTI;
			DominioSimNao achou = this.getPesquisaInternacaoRN().verificarCaracteristicaDaUnidadeFuncional(pUnfSeq, caracteristica);
			
			if (DominioSimNao.N.equals(achou)) {
				listaInternacoes.remove(i);
			}
		}
		
		cti = listaInternacoes.size();
		
		for (AinInternacao ainInternacao : listaInternacoes) {
			AinTiposCaraterInternacao tipoCaracterInternacao = ainInternacao.getTipoCaracterInternacao();
			Integer codigo = tipoCaracterInternacao.getCodigo();
			if (codigo.intValue() == 2) {
				adm++;
			} else {
				ast++;
			}
		}
			    
		return new ContaCtiNewVO(cti, adm, ast);
	}
	
	/**
	 * ORADB AINK_PES_REF_PRO.conta_aptos_new
	 * 
	 * @param vinculo
	 * @param matricula
	 * @param especialidade
	 * @return
	 */
	/* Conta internações em acomodação apartamentos */
	public ContaAptosNewVO contaAptosNew(Short vinculo, Integer matricula, Short especialidade) {
		Integer aptos = null;
		Integer adm = 0;
		Integer ast = 0;

		List<AinInternacao> listaInternacoes = getAinInternacaoDAO().internacoesAcomodacaoApartamento(vinculo, matricula,
				especialidade);
		aptos = listaInternacoes.size(); // nvl(count(*),0);

		for (AinInternacao ainInternacao : listaInternacoes) {
			AinTiposCaraterInternacao tipoCaracterInternacao = ainInternacao.getTipoCaracterInternacao();
			Integer codigo = tipoCaracterInternacao.getCodigo();
			if (codigo.intValue() == 2) {
				adm++;
			} else {
				ast++;
			}
		}

		return new ContaAptosNewVO(aptos, adm, ast);
	}
	
	/* Conta internações para a clínica em questão onde os leitos ocupados são de
	clínica diferente, não são apartamentos e nem cti */
	public ContaOutrasClinicasNewVO contaOutrasClinicasNew(Short vinculo, Integer matricula, Short especialidade) {
		Integer outrasClinicas = null;
		Integer adm = 0;
		Integer ast = 0;

		List<AinInternacao> listaInternacoes = getAinInternacaoDAO().internacoesOutraClinicasNaoAptoNaoCti(vinculo, matricula,
				especialidade, HOSPITAL_DIA_SIDA_AIDS);
		outrasClinicas = listaInternacoes.size(); // nvl(count(*),0);

		for (AinInternacao ainInternacao : listaInternacoes) {
			AinTiposCaraterInternacao tipoCaracterInternacao = ainInternacao.getTipoCaracterInternacao();
			Integer codigo = tipoCaracterInternacao.getCodigo();
			if (codigo.intValue() == 2) {
				adm++;
			} else {
				ast++;
			}
		}

		return new ContaOutrasClinicasNewVO(outrasClinicas, adm, ast);
	}
	
	// conta_outras_unidades_new
	/* Conta internações em leitos que não fazem parte do referencial, não são
	apartamentos e nem cti mas a clínica da internação é a mesma do leito */
	public ContaOutrasUnidadesNewVO contaOutrasUnidadesNew(Short vinculo, Integer matricula, Short especialidade) {
		Integer outrasUnidades = null;
		Integer adm = 0;
		Integer ast = 0;

		List<AinInternacao> listaInternacoes = getAinInternacaoDAO().internacoesMesmaClinicaNaoAptoNaoCti(vinculo, matricula,
				especialidade, HOSPITAL_DIA_SIDA_AIDS);
		outrasUnidades = listaInternacoes.size();

		for (AinInternacao ainInternacao : listaInternacoes) {
			AinTiposCaraterInternacao tipoCaracterInternacao = ainInternacao.getTipoCaracterInternacao();
			Integer codigo = tipoCaracterInternacao.getCodigo();
			if (codigo.intValue() == 2) {
				adm++;
			} else {
				ast++;
			}
		}

		return new ContaOutrasUnidadesNewVO(outrasUnidades, adm, ast);
	}
	
	protected PesquisaInternacaoRN getPesquisaInternacaoRN(){
		return pesquisaInternacaoRN;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO(){
		return ainInternacaoDAO;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
}